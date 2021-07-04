import 'dart:io';

import 'package:flutter/material.dart';
import 'package:music_player/music_player.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  List<MusicWithAlbumAndArtist> _musics = [];

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      bottomNavigationBar: Container(
        color: Colors.white,
        child: StreamBuilder<MusicWithAlbumAndArtist?>(
          initialData: MusicPlayer.music.value,
          stream: MusicPlayer.music.stream,
          builder: (context, snapshot) {
            final music = snapshot.data;
            return ListTile(
              title: Text(music?.music.title ?? ''),
              subtitle: Text(music?.artist?.title ?? ''),
              leading: AspectRatio(
                aspectRatio: 1,
                child: Image.file(
                  File(music?.album?.cover ?? ''),
                  errorBuilder: (_, ___, ____) {
                    return TextImage(
                      text: music?.music.title ?? music?.album?.title ?? '',
                    );
                  },
                ),
              ),
              trailing: StreamBuilder<PlayState?>(
                initialData: MusicPlayer.playState.value,
                stream: MusicPlayer.playState.stream,
                builder: (context, snapshot) {
                  final state = snapshot.data;
                  return InkWell(
                    child: Icon(state == PlayState.playing
                        ? Icons.pause
                        : Icons.play_arrow),
                    onTap: () {
                      if (state == PlayState.playing) {
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
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.search),
        onPressed: () {
          MusicPlayer.getAllMusic().then((value) {
            setState(() {
              _musics = value;
            });
          });
        },
      ),
      body: GridView.builder(
        itemCount: _musics.length,
        gridDelegate:
            const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 5),
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
      ),
    );
  }
}
