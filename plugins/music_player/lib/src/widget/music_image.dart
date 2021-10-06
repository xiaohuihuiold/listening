import 'dart:io';

import 'package:flutter/material.dart';

import '../../music_player.dart';

/// 当前播放音乐图片
class MusicImage extends StatelessWidget {
  const MusicImage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final themeData = Theme.of(context);
    return ValueListenableBuilder<MusicWithAlbumAndArtist?>(
      valueListenable: MusicPlayer.music,
      builder: (context, value, child) {
        final cover =
            value?.music.cover ?? value?.album?.cover ?? value?.artist?.cover;
        final title = value?.music.title;
        if (cover != null) {
          return AnimatedSwitcher(
            duration: const Duration(milliseconds: 400),
            child: SizedBox.expand(
              key: ValueKey(cover),
              child: Image.file(
                File(cover),
                fit: BoxFit.cover,
                errorBuilder: (_, __, ___) {
                  if (title == null) {
                    return Container(color: themeData.backgroundColor);
                  }
                  return TextImage(text: title);
                },
              ),
            ),
          );
        } else if (title != null) {
          return TextImage(text: title);
        }
        return Container(color: themeData.backgroundColor);
      },
    );
  }
}
