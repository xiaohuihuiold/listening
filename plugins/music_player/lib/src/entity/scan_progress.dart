import '../ext/map_ext.dart';

class ScanProgress {
  final String title;
  final int index;
  final int length;

  ScanProgress({
    required this.title,
    required this.index,
    required this.length,
  });

  factory ScanProgress.fromMap(Map map) {
    return ScanProgress(
      title: map.getString('title'),
      index: map.getInt('index'),
      length: map.getInt('length'),
    );
  }
}
