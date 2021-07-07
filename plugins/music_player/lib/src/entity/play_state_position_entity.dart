import '../ext/map_ext.dart';

class PlayStatePosition {
  final int time;
  final int position;
  final int duration;

  PlayStatePosition({
    required this.time,
    required this.position,
    required this.duration,
  });

  factory PlayStatePosition.fromMap(Map map) {
    return PlayStatePosition(
      time: map.getInt('time'),
      position: map.getInt('position'),
      duration: map.getInt('duration'),
    );
  }
}
