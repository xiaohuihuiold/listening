import 'package:flutter/material.dart';
import 'package:listening/page/scan/scan_provider.dart';
import 'package:music_player/music_player.dart';
import 'package:provider/provider.dart';

/// 扫描页面
class ScanPage extends StatefulWidget {
  const ScanPage({Key? key}) : super(key: key);

  static Future push(BuildContext context) async {
    return Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) {
          return ChangeNotifierProvider(
            create: (context) => ScanProvider(context),
            child: const ScanPage(),
          );
        },
      ),
    );
  }

  @override
  _ScanPageState createState() => _ScanPageState();
}

class _ScanPageState extends State<ScanPage> {
  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    final TextTheme textTheme = Theme.of(context).textTheme;
    final result = ValueListenableBuilder<ScanProgress?>(
      valueListenable: MusicPlayer.scanProgress,
      builder: (_, value, child) {
        double progress = 1.0;
        if (value != null) {
          progress = (value.index + 1) / value.length;
        }
        final texts = Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            if (value == null)
              Text(
                '开始扫描',
                style: textTheme.headline5,
              )
            else ...[
              Text(
                '${value.index + 1}/${value.length}',
                style: textTheme.headline5,
              ),
              Text(
                value.title,
                maxLines: 1,
                style: textTheme.caption,
                overflow: TextOverflow.ellipsis,
              ),
            ]
          ],
        );
        return InkWell(
          customBorder: const CircleBorder(),
          onTap: () {
            if (value != null) {
              return;
            }
            // 开始扫描
            ScanProvider.read(context).scan();
          },
          child: Stack(
            fit: StackFit.expand,
            alignment: Alignment.center,
            children: [
              CircularProgressIndicator(value: progress),
              IntrinsicWidth(
                child: texts,
              ),
            ],
          ),
        );
      },
    );
    return Scaffold(
      body: Center(
        child: Padding(
          padding: EdgeInsets.symmetric(horizontal: size.width / 4.0),
          child: AspectRatio(aspectRatio: 1.0, child: result),
        ),
      ),
    );
  }
}
