import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'entity/album_entity.dart';
import 'entity/artist_entity.dart';
import 'entity/playlist_entity.dart';
import 'entity/music_entity.dart';
import 'entity/play_state_position_entity.dart';
import 'entity/scan_progress.dart';

enum PlayState {
  none,
  stopped,
  paused,
  playing,
  fastForwarding,
  rewinding,
  buffering,
  error,
  connecting,
  skippingToPrevious,
  skippingToNext,
  skippingToQueueItem,
  playbackPositionUnknown,
}

class MusicSchema {
  static const _root = MusicSchema._('-1');
  static const music = MusicSchema._('music');
  static const allMusic = MusicSchema._('all_music');
  static const favoriteMusic = MusicSchema._('favorite_music');
  static const nowPlaylist = MusicSchema._('now_playlist');
  static const artist = MusicSchema._('artist');
  static const album = MusicSchema._('album');
  static const playlist = MusicSchema._('playlist');

  final String value;

  const MusicSchema._(this.value);
}

class MusicPlayer {
  static final MethodChannel _channel = const MethodChannel('music_player')
    ..setMethodCallHandler(_onMethodCall);
  static final EventChannel _eventChannel =
      const EventChannel('music_player/event')
        ..receiveBroadcastStream().listen(_onEvent);

  static final ValueNotifier<bool> connected = ValueNotifier(false);

  static final ValueNotifier<PlayState?> playState = ValueNotifier(null);

  static final ValueNotifier<MusicWithAlbumAndArtist?> music =
      ValueNotifier(null);

  static final ValueNotifier<List<MusicWithAlbumAndArtist>> nowPlaylist =
      ValueNotifier([]);

  static final ValueNotifier<ScanProgress?> scanProgress = ValueNotifier(null);

  static PlayStatePosition? _playStatePosition;

  static int get timeNow => DateTime.now().millisecondsSinceEpoch;

  static int get duration => _playStatePosition?.duration ?? 1;

  static int get position {
    final PlayStatePosition? state = _playStatePosition;
    if (state == null) {
      return 0;
    }
    int time = timeNow - state.time + state.position;
    if (playState.value != PlayState.playing) {
      time = state.position;
    }
    return time.clamp(0, duration);
  }

  static double get progress {
    double progress = 0.0;
    try {
      progress = position.toDouble() / duration.toDouble();
    } catch (e) {}
    return progress.clamp(0.0, 1.0);
  }

  static Future<dynamic> _onMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'connected':
        connected.value = call.arguments as bool;
        break;
      case 'playState':
        final int? index = call.arguments;
        if (index == null) {
          playState.value = null;
        } else {
          playState.value = PlayState.values[index];
        }
        break;
      case 'position':
        final Map? map = call.arguments;
        if (map == null) {
          _playStatePosition = null;
        } else {
          _playStatePosition = PlayStatePosition.fromMap(map);
        }
        break;
      case 'music':
        final Map? map = call.arguments;
        if (map == null) {
          music.value = null;
        } else {
          music.value = MusicWithAlbumAndArtist.fromMap(map);
        }
        break;
      case 'nowPlaylist':
        final List list = call.arguments;
        nowPlaylist.value =
            list.map((e) => MusicWithAlbumAndArtist.fromMap(e)).toList();
        break;
      case 'scanProgress':
        final Map? map = call.arguments;
        if (map == null) {
          scanProgress.value = null;
        } else {
          scanProgress.value = ScanProgress.fromMap(map);
        }
        break;
    }
  }

  static void _onEvent(dynamic event) {
    print(event);
  }

  static Future<void> scan() async {
    await _channel.invokeMethod('scan');
  }

  static Future<void> play({MusicSchema? schema, int? id, int? musicId}) async {
    String? parentId;
    if (schema != null) {
      parentId = '${schema.value}:${id ?? MusicSchema._root.value}';
    }
    String? childId;
    if (musicId != null) {
      childId = '${MusicSchema.music.value}:$musicId';
    }
    await _channel.invokeMethod('play', {
      'parentId': parentId,
      'childId': childId,
    });
  }

  static Future<void> pause() async {
    await _channel.invokeMethod('pause');
  }

  static Future<void> skipToPrevious() async {
    await _channel.invokeMethod('skipToPrevious');
  }

  static Future<void> skipToNext() async {
    await _channel.invokeMethod('skipToNext');
  }

  static Future<void> seekTo(int position) async {
    await _channel.invokeMethod('seekTo', position);
  }

  static Future<List<PlaylistWithCounts>> getPlaylist() async {
    final List? list = await _channel.invokeMethod<List>('getPlaylist');
    if (list == null) {
      return [];
    }
    return list.map((e) => PlaylistWithCounts.fromMap(e)).toList();
  }

  static Future<List<AlbumWithCounts>> getAlbums() async {
    final List? list = await _channel.invokeMethod<List>('getAlbums');
    if (list == null) {
      return [];
    }
    return list.map((e) => AlbumWithCounts.fromMap(e)).toList();
  }

  static Future<List<ArtistWithCounts>> getArtists() async {
    final List? list = await _channel.invokeMethod<List>('getArtists');
    if (list == null) {
      return [];
    }
    return list.map((e) => ArtistWithCounts.fromMap(e)).toList();
  }

  static Future<List<MusicWithAlbumAndArtist>> getAllMusic() async {
    final List? list = await _channel.invokeMethod<List>('getAllMusic');
    if (list == null) {
      return [];
    }
    return list.map((e) => MusicWithAlbumAndArtist.fromMap(e)).toList();
  }

  static Future<List<MusicWithAlbumAndArtist>> getFavoriteMusic() async {
    final List? list = await _channel.invokeMethod<List>('getFavoriteMusic');
    if (list == null) {
      return [];
    }
    return list.map((e) => MusicWithAlbumAndArtist.fromMap(e)).toList();
  }

  static Future<List<MusicWithAlbumAndArtist>> getNowPlaylist() async {
    final List? list = await _channel.invokeMethod<List>('getNowPlaylist');
    if (list == null) {
      return [];
    }
    return list.map((e) => MusicWithAlbumAndArtist.fromMap(e)).toList();
  }

  static Future<List<MusicWithAlbumAndArtist>> getMusics(
      String schemaId) async {
    final List? list = await _channel.invokeMethod<List>('getMusics', schemaId);
    if (list == null) {
      return [];
    }
    return list.map((e) => MusicWithAlbumAndArtist.fromMap(e)).toList();
  }

  static Future<void> refreshState() async {
    await _channel.invokeMethod('refreshState');
  }
}
