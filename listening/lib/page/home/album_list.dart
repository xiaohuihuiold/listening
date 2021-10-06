import 'dart:io';

import 'package:flutter/material.dart';
import 'package:listening/page/home/home_provider.dart';
import 'package:music_player/music_player.dart';

/// 专辑列表
class AlbumList extends StatefulWidget {
  const AlbumList({Key? key}) : super(key: key);

  @override
  _AlbumListState createState() => _AlbumListState();
}

class _AlbumListState extends State<AlbumList>
    with AutomaticKeepAliveClientMixin {
  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    final themeData = Theme.of(context);
    return ValueListenableBuilder<List<AlbumWithCounts>>(
      valueListenable: HomeProvider.watch(context).albums,
      builder: (context, value, child) {
        return GridView.builder(
          itemCount: value.length,
          padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 8.0),
          physics: const BouncingScrollPhysics(),
          gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
            maxCrossAxisExtent: 200.0,
            crossAxisSpacing: 8.0,
            mainAxisSpacing: 8.0,
          ),
          itemBuilder: (_, index) {
            final album = value[index];
            Widget result;
            final cover = album.album.cover;
            final title = album.album.title ?? album.album.key;
            if (cover != null) {
              result = Image.file(
                File(cover),
                key: ValueKey(cover),
                fit: BoxFit.cover,
                errorBuilder: (_, __, ___) {
                  return TextImage(text: title);
                },
              );
            } else {
              result = TextImage(text: title);
            }
            return Card(
              clipBehavior: Clip.hardEdge,
              child: Stack(
                children: [
                  Positioned.fill(child: result),
                  const Positioned.fill(
                    child: DecoratedBox(
                      decoration: BoxDecoration(
                        gradient: LinearGradient(
                          begin: Alignment.bottomCenter,
                          end: Alignment.topCenter,
                          colors: [
                            Colors.black38,
                            Colors.transparent,
                          ],
                        ),
                      ),
                    ),
                  ),
                  Positioned(
                    bottom: 0.0,
                    left: 0.0,
                    right: 0.0,
                    child: Row(
                      children: [
                        const SizedBox(width: 8.0),
                        const Icon(
                          Icons.queue_music_outlined,
                          size: 22,
                          color: Colors.white,
                        ),
                        const SizedBox(width: 4.0),
                        Text(
                          '${album.counts}',
                          style: themeData.textTheme.subtitle1?.copyWith(
                            color: Colors.white,
                          ),
                        ),
                        const Spacer(),
                        Material(
                          color: Colors.transparent,
                          child: IconButton(
                            color: Colors.white,
                            icon: const Icon(Icons.play_arrow),
                            onPressed: () {
                              MusicPlayer.play(
                                schema: MusicSchema.album,
                                id: album.album.id,
                              );
                            },
                          ),
                        )
                      ],
                    ),
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }
}
