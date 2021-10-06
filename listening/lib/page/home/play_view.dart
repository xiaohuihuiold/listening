import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:listening/widget/palette_builder.dart';
import 'package:music_player/music_player.dart';

/// 播放视图
class PlayView extends StatefulWidget {
  /// 是否横屏
  final bool isHorizontal;

  const PlayView({
    Key? key,
    required this.isHorizontal,
  }) : super(key: key);

  @override
  _PlayViewState createState() => _PlayViewState();
}

class _PlayViewState extends State<PlayView>
    with AutomaticKeepAliveClientMixin {
  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return widget.isHorizontal ? _HorizontalPlayView() : const SizedBox();
  }
}

class _HorizontalPlayView extends StatefulWidget {
  const _HorizontalPlayView({Key? key}) : super(key: key);

  @override
  _HorizontalPlayViewState createState() => _HorizontalPlayViewState();
}

class _HorizontalPlayViewState extends State<_HorizontalPlayView> {
  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        const Expanded(child: _PlayCard()),
        Expanded(
          child: ValueListenableBuilder<MusicWithAlbumAndArtist?>(
            valueListenable: MusicPlayer.music,
            builder: (context, now, child) {
              return ValueListenableBuilder<List<MusicWithAlbumAndArtist>>(
                valueListenable: MusicPlayer.nowPlaylist,
                builder: (_, value, child) {
                  return ListView.builder(
                    itemCount: value.length,
                    physics: const BouncingScrollPhysics(),
                    itemBuilder: (_, index) {
                      final music = value[index];
                      Widget image;
                      final cover = music.music.cover ??
                          music.album?.cover ??
                          music.artist?.cover;
                      final title = music.music.title ?? 'Unknown';
                      final album = music.album?.title ?? 'Unknown';
                      final artist = music.artist?.title ?? 'Unknown';
                      if (cover != null) {
                        image = Image.file(
                          File(cover),
                          key: ValueKey(cover),
                          fit: BoxFit.cover,
                          errorBuilder: (_, __, ___) {
                            return TextImage(text: title);
                          },
                        );
                      } else {
                        image = TextImage(text: title);
                      }
                      if (now?.music.id == music.music.id) {
                        image = Stack(
                          fit: StackFit.expand,
                          children: [
                            image,
                            Container(
                              color: Colors.black38,
                              alignment: Alignment.center,
                              child: const Icon(
                                Icons.pause,
                                color: Colors.white,
                                size: 24.0,
                              ),
                            ),
                          ],
                        );
                      }
                      return ListTile(
                        title: Text(title),
                        subtitle: Text(artist),
                        leading: AspectRatio(
                          aspectRatio: 1.0,
                          child: Card(
                            clipBehavior: Clip.hardEdge,
                            child: image,
                          ),
                        ),
                        trailing: Text(music.music.durationStr),
                        onTap: () {
                          MusicPlayer.play(
                            schema: MusicSchema.nowPlaylist,
                            musicId: music.music.id,
                          );
                        },
                      );
                    },
                  );
                },
              );
            },
          ),
        ),
      ],
    );
  }
}

class _PlayCard extends StatefulWidget {
  const _PlayCard({Key? key}) : super(key: key);

  @override
  _PlayCardState createState() => _PlayCardState();
}

class _PlayCardState extends State<_PlayCard> {
  @override
  Widget build(BuildContext context) {
    final themeData = Theme.of(context);
    final result = ValueListenableBuilder<MusicWithAlbumAndArtist?>(
      valueListenable: MusicPlayer.music,
      builder: (context, value, child) {
        final cover =
            value?.music.cover ?? value?.album?.cover ?? value?.artist?.cover;
        final control = Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            IconButton(
              iconSize: 48,
              icon: const Icon(Icons.skip_previous),
              onPressed: () => MusicPlayer.skipToPrevious(),
            ),
            ValueListenableBuilder<PlayState?>(
              valueListenable: MusicPlayer.playState,
              builder: (context, value, child) {
                if (value != PlayState.playing) {
                  return IconButton(
                    iconSize: 58,
                    icon: const Icon(Icons.play_circle),
                    onPressed: () {
                      MusicPlayer.play();
                    },
                  );
                }
                return IconButton(
                  iconSize: 58,
                  icon: const Icon(Icons.pause_circle),
                  onPressed: () {
                    MusicPlayer.pause();
                  },
                );
              },
            ),
            IconButton(
              iconSize: 48,
              icon: const Icon(Icons.skip_next),
              onPressed: () => MusicPlayer.skipToNext(),
            ),
          ],
        );
        Widget result = Column(
          children: [
            Expanded(
              flex: 2,
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 12.0),
                child: AspectRatio(
                  aspectRatio: 1.0,
                  child: Card(
                    clipBehavior: Clip.hardEdge,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: const MusicImage(),
                  ),
                ),
              ),
            ),
            Expanded(
              flex: 1,
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text(
                          '${value?.music.title ?? 'Unknown'}-${value?.artist?.title ?? 'Unknown'}',
                          style: const TextStyle(fontSize: 18.0),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                        Text(
                          value?.album?.title ?? 'Unknown',
                          style: const TextStyle(fontSize: 14.0),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ],
                    ),
                    const _TimerSeekBar(),
                    control,
                  ],
                ),
              ),
            ),
          ],
        );
        if (cover == null) {
          return Card(
            elevation: 0,
            clipBehavior: Clip.hardEdge,
            color: themeData.cardColor.withOpacity(0.6),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
            ),
            child: result,
          );
        }
        return AnimatedSwitcher(
          duration: const Duration(milliseconds: 400),
          child: SizedBox.expand(
            key: ValueKey(cover),
            child: PaletteBuilder(
              FileImage(File(cover)),
              key: ValueKey(cover),
              builder: (_, generator, child) {
                final paletteColor = generator?.vibrantColor;
                return Card(
                  elevation: 0,
                  clipBehavior: Clip.hardEdge,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16),
                  ),
                  color: paletteColor?.color.withOpacity(0.6),
                  child: SliderTheme(
                    data: SliderThemeData(
                      trackHeight: 3,
                      thumbColor: paletteColor?.bodyTextColor,
                      activeTrackColor: paletteColor?.bodyTextColor,
                      inactiveTrackColor: paletteColor?.titleTextColor,
                      thumbShape:
                          const RoundSliderThumbShape(enabledThumbRadius: 6),
                    ),
                    child: IconTheme(
                      data: IconThemeData(color: paletteColor?.bodyTextColor),
                      child: DefaultTextStyle(
                        style: TextStyle(color: paletteColor?.titleTextColor),
                        child: child!,
                      ),
                    ),
                  ),
                );
              },
              child: result,
            ),
          ),
        );
      },
    );
    return Container(
      width: double.infinity,
      height: double.infinity,
      padding: const EdgeInsets.all(24.0),
      child: result,
    );
  }
}

class _TimerSeekBar extends StatefulWidget {
  const _TimerSeekBar({Key? key}) : super(key: key);

  @override
  _TimerSeekBarState createState() => _TimerSeekBarState();
}

class _TimerSeekBarState extends State<_TimerSeekBar> {
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
    return Slider(
      value: MusicPlayer.progress,
      onChanged: (value) {},
    );
  }
}
