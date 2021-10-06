import 'dart:io';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:listening/common/config/theme_config.dart';
import 'package:listening/page/home/home_page.dart';
import 'package:listening/page/home/home_provider.dart';
import 'package:music_player/music_player.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    MusicPlayer.refreshState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Listening',
      darkTheme: ThemeConfig.dark,
      theme: ThemeConfig.light,
      initialRoute: '/',
      home: ChangeNotifierProvider<HomeProvider>(
        create: (context) => HomeProvider(context),
        child: const HomePage(),
      ),
    );
  }
}
