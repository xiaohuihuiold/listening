import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:music_player/music_player.dart';

/// 播放控制栏
class MusicBar extends StatefulWidget {
  const MusicBar({Key? key}) : super(key: key);

  @override
  _MusicBarState createState() => _MusicBarState();
}

class _MusicBarState extends State<MusicBar> {
  @override
  Widget build(BuildContext context) {
    final themeData = Theme.of(context);
    final isDark = themeData.brightness == Brightness.dark;
    final control = Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        IconButton(
          color: isDark ? themeData.primaryColorLight : themeData.primaryColor,
          icon: const Icon(Icons.skip_previous),
          onPressed: () => MusicPlayer.skipToPrevious(),
        ),
        ValueListenableBuilder<PlayState?>(
          valueListenable: MusicPlayer.playState,
          builder: (context, value, child) {
            if (value != PlayState.playing) {
              return IconButton(
                iconSize: 32.0,
                color: isDark
                    ? themeData.primaryColorLight
                    : themeData.primaryColorDark,
                icon: const Icon(Icons.play_circle),
                onPressed: () {
                  MusicPlayer.play();
                },
              );
            }
            return IconButton(
              iconSize: 32.0,
              color: isDark
                  ? themeData.primaryColorLight
                  : themeData.primaryColorDark,
              icon: const Icon(Icons.pause_circle),
              onPressed: () {
                MusicPlayer.pause();
              },
            );
          },
        ),
        IconButton(
          color: isDark ? themeData.primaryColorLight : themeData.primaryColor,
          icon: const Icon(Icons.skip_next),
          onPressed: () => MusicPlayer.skipToNext(),
        ),
      ],
    );
    Widget result = Row(
      children: [
        const SizedBox(width: 4.0),
        AspectRatio(
          aspectRatio: 1.0,
          child: Card(
            clipBehavior: Clip.hardEdge,
            color: themeData.backgroundColor,
            child: const MusicImage(),
          ),
        ),
        const SizedBox(width: 4.0),
        Expanded(
          child: ValueListenableBuilder<MusicWithAlbumAndArtist?>(
            valueListenable: MusicPlayer.music,
            builder: (context, value, child) {
              return Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    value?.music.title ?? 'Unknown',
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: themeData.textTheme.subtitle1,
                  ),
                  Text(
                    value?.artist?.title ?? 'Unknown',
                    maxLines: 1,
                    overflow: TextOverflow.fade,
                    style: themeData.textTheme.caption,
                  ),
                ],
              );
            },
          ),
        ),
        control,
      ],
    );
    result = IntrinsicHeight(child: result);
    const seekBar = _TimerProgress();
    return SizedBox(
      height: kToolbarHeight + 1.0,
      child: Card(
        margin: EdgeInsets.zero,
        elevation: 0,
        color: themeData.cardColor.withOpacity(0.6),
        shape: const RoundedRectangleBorder(),
        child: Column(
          children: [
            seekBar,
            Expanded(child: result),
          ],
        ),
      ),
    );
  }
}

class _TimerProgress extends StatefulWidget {
  const _TimerProgress({Key? key}) : super(key: key);

  @override
  _TimerProgressState createState() => _TimerProgressState();
}

class _TimerProgressState extends State<_TimerProgress> {
  Timer? _timer;

  void _startTimer() {
    _stopTimer();
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (mounted) {
        setState(() {});
      }
    });
  }

  void _stopTimer() {
    _timer?.cancel();
    _timer = null;
  }

  @override
  void initState() {
    super.initState();
    _startTimer();
  }

  @override
  void dispose() {
    _stopTimer();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return LinearProgressIndicator(
      minHeight: 1.0,
      value: MusicPlayer.progress,
    );
  }
}
