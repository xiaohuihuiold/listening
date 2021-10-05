import 'package:flutter/material.dart';
import 'package:listening/page/home/home_provider.dart';
import 'package:music_player/music_player.dart';

/// 主页
class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  void initState() {
    super.initState();
    HomeProvider.read(context).init();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          MusicPlayer.play(parentId: 'all_music:-1');
        },
      ),
    );
  }
}
