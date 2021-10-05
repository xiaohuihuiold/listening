import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:music_player/music_player.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:provider/provider.dart';

/// 扫描provider
class ScanProvider extends ChangeNotifier {
  final BuildContext context;

  ScanProvider(this.context) {
    MusicPlayer.scanProgress.addListener(_onScan);
  }

  /// 扫描歌曲
  Future<void> scan() async {
    PermissionStatus status = await Permission.storage.status;
    if (status.isDenied) {
      status = await Permission.storage.request();
      if (status.isDenied) {
        Fluttertoast.showToast(msg: '存储权限拒绝');
        return;
      }
    }
    Fluttertoast.showToast(msg: '存储权限已授予');
    await MusicPlayer.scan();
  }

  /// 扫描回调
  void _onScan() {
    if (MusicPlayer.scanProgress.value == null) {
      // 扫描完成
      Navigator.of(context).pop();
    }
  }

  @override
  void dispose() {
    MusicPlayer.scanProgress.removeListener(_onScan);
    super.dispose();
  }

  static ScanProvider read(BuildContext context) {
    return context.read<ScanProvider>();
  }

  static ScanProvider watch(BuildContext context) {
    return context.watch<ScanProvider>();
  }
}
