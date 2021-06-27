
import 'dart:async';

import 'package:flutter/services.dart';

class MusicPlayer {
  static const MethodChannel _channel = MethodChannel('music_player');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
