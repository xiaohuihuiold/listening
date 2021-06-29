import 'dart:async';

import 'package:flutter/services.dart';

class MusicPlayer {
  static const MethodChannel _channel = MethodChannel('music_player');

  static Future<void> scan() async {
    await _channel.invokeMethod('scan');
  }
}
