import '../ext/map_ext.dart';

class BaseTime {
  final int addTime;
  final int updateTime;

  const BaseTime({
    required this.addTime,
    required this.updateTime,
  });

  factory BaseTime.fromMap(Map map) {
    return BaseTime(
      addTime: map.getInt('add_time'),
      updateTime: map.getInt('update_time'),
    );
  }
}
