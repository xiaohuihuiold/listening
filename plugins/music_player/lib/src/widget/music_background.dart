import 'dart:io';
import 'dart:ui';

import 'package:flutter/material.dart';

import '../entity/music_entity.dart';
import '../music_player.dart';

/// 音乐背景
class MusicBackground extends StatelessWidget {
  final Widget? child;

  const MusicBackground({
    Key? key,
    this.child,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final background = ValueListenableBuilder<MusicWithAlbumAndArtist?>(
      valueListenable: MusicPlayer.music,
      builder: (_, value, child) {
        final cover =
            value?.music.cover ?? value?.album?.cover ?? value?.artist?.cover;
        final color = isDark ? Colors.grey[850]! : Colors.grey[50]!;
        if (cover == null) {
          return Container(color: color);
        }
        return Stack(
          fit: StackFit.expand,
          alignment: Alignment.center,
          children: [
            ImageFiltered(
              imageFilter: ImageFilter.blur(sigmaX: 50.0, sigmaY: 50.0),
              child: AnimatedSwitcher(
                duration: const Duration(milliseconds: 400),
                child: SizedBox.expand(
                  key: ValueKey(cover),
                  child: Image.file(
                    File(cover),
                    fit: BoxFit.cover,
                  ),
                ),
              ),
            ),
            Container(color: color.withOpacity(0.8)),
          ],
        );
      },
    );
    return Stack(
      fit: StackFit.expand,
      alignment: Alignment.center,
      children: [background, if (child != null) child!],
    );
  }
}
