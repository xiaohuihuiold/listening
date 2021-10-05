import 'package:flutter/material.dart';

/// 主题配置
class ThemeConfig {
  static final light = ThemeData(
    primarySwatch: Colors.teal,
    scaffoldBackgroundColor: Colors.transparent,
  );
  static final dark = ThemeData(
    brightness: Brightness.dark,
    scaffoldBackgroundColor: Colors.transparent,
  );
}
