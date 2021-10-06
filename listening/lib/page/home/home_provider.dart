import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:listening/page/scan/scan_page.dart';
import 'package:music_player/music_player.dart';
import 'package:provider/provider.dart';

/// 主页provider
class HomeProvider extends ChangeNotifier {
  final BuildContext context;

  /// 所有音乐
  final ValueNotifier<List<MusicWithAlbumAndArtist>> musics = ValueNotifier([]);

  /// 所有专辑
  final ValueNotifier<List<AlbumWithCounts>> albums = ValueNotifier([]);

  HomeProvider(this.context) {
    MusicPlayer.scanProgress.addListener(_onScan);
  }

  /// 初始化
  Future<void> init() async {
    final musics = await MusicPlayer.getAllMusic();
    if (musics.isEmpty) {
      ScanPage.push(context);
    } else {
      refreshMusics();
      refreshAlbums();
    }
  }

  /// 刷新音乐数据
  Future<void> refreshMusics() async {
    musics.value = await MusicPlayer.getAllMusic();
  }

  /// 刷新专辑数据
  Future<void> refreshAlbums() async {
    albums.value = await MusicPlayer.getAlbums();
  }

  /// 扫描回调
  void _onScan() {
    if (MusicPlayer.scanProgress.value == null) {
      // 扫描完成
      Fluttertoast.showToast(msg: '扫描完成');
      refreshMusics();
      refreshAlbums();
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
