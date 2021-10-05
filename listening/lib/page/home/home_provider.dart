import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:listening/page/scan/scan_page.dart';
import 'package:music_player/music_player.dart';
import 'package:provider/provider.dart';

/// 主页provider
class HomeProvider extends ChangeNotifier {
  final BuildContext context;

  HomeProvider(this.context) {
    MusicPlayer.scanProgress.addListener(_onScan);
  }

  /// 初始化
  Future<void> init() async {
    final musics = await MusicPlayer.getAllMusic();
    if (musics.isEmpty) {
      ScanPage.push(context);
    }
  }

  /// 扫描回调
  void _onScan() {
    if (MusicPlayer.scanProgress.value == null) {
      // 扫描完成
      Fluttertoast.showToast(msg: '扫描完成');
    }
  }

  @override
  void dispose() {
    MusicPlayer.scanProgress.removeListener(_onScan);
    super.dispose();
  }

  static HomeProvider read(BuildContext context) {
    return context.read<HomeProvider>();
  }

  static HomeProvider watch(BuildContext context) {
    return context.watch<HomeProvider>();
  }
}
