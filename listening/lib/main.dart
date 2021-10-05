import 'dart:io';
import 'dart:ui';

import 'package:flutter/cupertino.dart';
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

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
  @override
  void didChangePlatformBrightness() {
    super.didChangePlatformBrightness();
    setState(() {});
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance?.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance?.removeObserver(this);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final isDark =
        WidgetsBinding.instance?.window.platformBrightness == Brightness.dark;
    return Stack(
      fit: StackFit.expand,
      alignment: Alignment.center,
      textDirection: TextDirection.ltr,
      children: [
        ImageFiltered(
          imageFilter: ImageFilter.blur(sigmaX: 50.0, sigmaY: 50.0),
          child: ValueListenableBuilder<MusicWithAlbumAndArtist?>(
            valueListenable: MusicPlayer.music,
            builder: (_, value, child) {
              final cover = value?.music.cover ??
                  value?.album?.cover ??
                  value?.artist?.cover;
              if (cover == null) {
                return const SizedBox();
              }
              return Image.file(
                File(cover),
                key: ValueKey(cover),
                fit: BoxFit.cover,
              );
            },
          ),
        ),
        ValueListenableBuilder<MusicWithAlbumAndArtist?>(
          valueListenable: MusicPlayer.music,
          builder: (_, value, child) {
            final cover = value?.music.cover ??
                value?.album?.cover ??
                value?.artist?.cover;
            final color = isDark ? Colors.grey[850]! : Colors.grey[50]!;
            return Container(
              color: color.withOpacity(cover == null ? 1.0 : 0.8),
            );
          },
        ),
        MaterialApp(
          title: 'Listening',
          darkTheme: ThemeConfig.dark,
          theme: ThemeConfig.light,
          initialRoute: '/',
          home: ChangeNotifierProvider<HomeProvider>(
            create: (context) => HomeProvider(context),
            child: const HomePage(),
          ),
        ),
      ],
    );
  }
}
