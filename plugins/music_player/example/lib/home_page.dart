import 'package:flutter/material.dart';
import 'package:music_player/music_player.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        child: ElevatedButton(
          child: const Text('scan'),
          onPressed: () {
            MusicPlayer.scan();
          },
        ),
      ),
    );
  }
}
