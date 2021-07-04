import 'dart:async';

import 'package:flutter/services.dart';

import 'entity/album_entity.dart';
import 'entity/artist_entity.dart';
import 'entity/playlist_entity.dart';
import 'entity/music_entity.dart';

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

/// 带初始值的流
class StreamControllerState<T> {
  final StreamController<T> _controller = StreamController.broadcast();

  Stream<T> get stream => _controller.stream;

  T? value;

  void _add(T value) {
    this.value = value;
    _controller.add(value);
  }
}

class MusicPlayer {
  static final MethodChannel _channel = const MethodChannel('music_player')
    ..setMethodCallHandler(_onMethodCall);
  static final EventChannel _eventChannel =
      const EventChannel('music_player/event')
        ..receiveBroadcastStream().listen(_onEvent);

  static final StreamControllerState<bool> connected = StreamControllerState();

  static final StreamControllerState<PlayState?> playState =
      StreamControllerState();

  static final StreamControllerState<MusicWithAlbumAndArtist?> music =
      StreamControllerState();

  static final StreamControllerState<List<MusicWithAlbumAndArtist>>
      nowPlaylist = StreamControllerState();

  static Future<dynamic> _onMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'connected':
        connected._add(call.arguments as bool);
        break;
      case 'playState':
        final int? index = call.arguments;
        if (index == null) {
          playState._add(null);
        } else {
          playState._add(PlayState.values[index]);
        }
        break;
      case 'music':
        final Map? map = call.arguments;
        if (map == null) {
          music._add(null);
        } else {
          music._add(MusicWithAlbumAndArtist.fromMap(map));
        }
        break;
      case 'nowPlaylist':
        final List list = call.arguments;
        nowPlaylist
            ._add(list.map((e) => MusicWithAlbumAndArtist.fromMap(e)).toList());
        break;
    }
  }

  static void _onEvent(dynamic event) {
    print(event);
  }

  static Future<void> scan() async {
    await _channel.invokeMethod('scan');
  }

  static Future<void> play({String? parentId, String? childId}) async {
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
