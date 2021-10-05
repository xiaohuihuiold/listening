import 'dart:io';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:music_player/music_player.dart';
import 'package:music_player_example/page/home/home_page.dart';

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
      theme: ThemeData.light().copyWith(
        scaffoldBackgroundColor: Colors.transparent,
        backgroundColor: Colors.transparent,
        appBarTheme: const AppBarTheme(
          elevation: 0.0,
          backgroundColor: Colors.transparent,
        ),
      ),
      darkTheme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: Colors.transparent,
        backgroundColor: Colors.transparent,
        appBarTheme: const AppBarTheme(
          elevation: 0.0,
          backgroundColor: Colors.transparent,
        ),
      ),
      home: HomePage(),
      builder: (context, child) {
        return Stack(
          fit: StackFit.expand,
          children: [
            ValueListenableBuilder<MusicWithAlbumAndArtist?>(
              valueListenable: MusicPlayer.music,
              builder: (_, value, child) {
                return Image.file(
                  File(value?.album?.cover ?? ''),
                  fit: BoxFit.cover,
                );
              },
            ),
            Container(
              decoration: BoxDecoration(
                  color: (Theme.of(context).brightness == Brightness.dark
                          ? Colors.black
                          : Colors.white)
                      .withOpacity(0.8)),
            ),
            if (child != null)
              BackdropFilter(
                filter: ImageFilter.blur(sigmaX: 50.0, sigmaY: 50.0),
                child: child,
              ),
          ],
        );
      },
    );
  }
}
