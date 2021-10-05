import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:music_player/music_player.dart';

class HomePageTemp extends StatefulWidget {
  const HomePageTemp({Key? key}) : super(key: key);

  @override
  _HomePageTempState createState() => _HomePageTempState();
}

class _HomePageTempState extends State<HomePageTemp> {
  List<MusicWithAlbumAndArtist> _musics = [];

  @override
  void initState() {
    super.initState();
    MusicPlayer.getAllMusic().then((value) {
      setState(() {
        _musics = value;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      bottomNavigationBar: const _MusicController(),
      floatingActionButton: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          FloatingActionButton(
            child: const Icon(Icons.search),
            onPressed: () {
              MusicPlayer.scan();
            },
          ),
        ],
      ),
      body: ValueListenableBuilder<ScanProgress?>(
          valueListenable: MusicPlayer.scanProgress,
          builder: (context, value, child) {
            if (value != null) {
              return Container(
                width: double.infinity,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: Text(
                            value.title,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        SizedBox(width: 12.0),
                        Text('${value.index + 1}/${value.length}'),
                      ],
                    ),
                    LinearProgressIndicator(
                      value: (value.index + 1) / value.length,
                    ),
                  ],
                ),
              );
            }
            return GridView.builder(
              itemCount: _musics.length,
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2),
              itemBuilder: (context, index) {
                final item = _musics[index];
                return InkWell(
                  onTap: () {
                    MusicPlayer.play(
                      parentId: 'all_music:-1',
                      childId: 'music:${item.music.id.toString()}',
                    );
                  },
                  child: Card(
                    clipBehavior: Clip.antiAlias,
                    child: Image.file(
                      File(item.album?.cover ?? ''),
                      errorBuilder: (_, ___, ____) {
                        return TextImage(
                          text: item.music.title ?? item.album?.title ?? '',
                        );
                      },
                    ),
                  ),
                );
              },
            );
          }),
    );
  }
}

class _MusicController extends StatefulWidget {
  const _MusicController({Key? key}) : super(key: key);

  @override
  __MusicControllerState createState() => __MusicControllerState();
}

class __MusicControllerState extends State<_MusicController> {
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _timer = Timer.periodic(Duration(milliseconds: 10), (timer) {
      if (mounted) {
        setState(() {});
      }
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    _timer = null;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).backgroundColor,
        boxShadow: [BoxShadow(color: Colors.grey, blurRadius: 8)],
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          LinearProgressIndicator(
            value: MusicPlayer.position / MusicPlayer.duration,
          ),
          ValueListenableBuilder<MusicWithAlbumAndArtist?>(
            valueListenable: MusicPlayer.music,
            builder: (context, value, child) {
              return ListTile(
                title: Text(value?.music.title ?? ''),
                subtitle: Text(value?.artist?.title ?? ''),
                leading: AspectRatio(
                  aspectRatio: 1,
                  child: Image.file(
                    File(value?.album?.cover ?? ''),
                    errorBuilder: (_, ___, ____) {
                      return TextImage(
                        text: value?.music.title ?? value?.album?.title ?? '',
                      );
                    },
                  ),
                ),
                trailing: ValueListenableBuilder<PlayState?>(
                  valueListenable: MusicPlayer.playState,
                  builder: (context, value, child) {
                    return InkWell(
                      child: Icon(value == PlayState.playing
                          ? Icons.pause
                          : Icons.play_arrow),
                      onTap: () {
                        if (value == PlayState.playing) {
                          MusicPlayer.pause();
                        } else {
                          MusicPlayer.play();
                        }
                      },
                    );
                  },
                ),
              );
            },
          ),
        ],
      ),
    );
  }
}
