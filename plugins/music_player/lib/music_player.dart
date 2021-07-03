import 'dart:async';

import 'package:flutter/services.dart';

class MusicPlayer {
  static final MethodChannel _channel = const MethodChannel('music_player')
    ..setMethodCallHandler(_onMethodCall);
  static final EventChannel _eventChannel = const EventChannel('music_player')
    ..receiveBroadcastStream().listen(_onEvent);

  static StreamController<bool> connected = StreamController.broadcast();

  static Future<dynamic> _onMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'connected':
        break;
      case 'playState':
        break;
      case 'music':
        break;
      case 'nowPlaylist':
        break;
    }
    print(call.arguments);
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

  static Future<dynamic> getMusic() async {}

  static Future<dynamic> getPlaylist() async {}

  static Future<dynamic> getAlbums() async {}

  static Future<dynamic> getArtists() async {}

  static Future<dynamic> getAllMusic() async {}

  static Future<dynamic> getFavoriteMusic() async {}

  static Future<dynamic> getNowPlaylist() async {}

  static Future<dynamic> getMusics() async {}
}
