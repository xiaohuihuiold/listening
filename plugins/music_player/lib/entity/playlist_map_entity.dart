import 'base_time.dart';
import 'music_entity.dart';
import 'playlist_map_entity.dart';
import '../map_ext.dart';

class PlaylistMap {
  final int id;
  final int playlistId;
  final int musicId;
  final BaseTime time;

  const PlaylistMap({
    required this.id,
    required this.playlistId,
    required this.musicId,
    required this.time,
  });

  factory PlaylistMap.fromMap(Map map) {
    return PlaylistMap(
      id: map.getInt('id"'),
      playlistId: map.getInt('playlist_id'),
      musicId: map.getInt('music_id'),
      time: BaseTime.fromMap(map),
    );
  }
}

class PlaylistMapWithMusicAndAlbumAndArtists {
  final PlaylistMap playlistMap;
  final MusicWithAlbumAndArtist music;

  const PlaylistMapWithMusicAndAlbumAndArtists({
    required this.playlistMap,
    required this.music,
  });

  factory PlaylistMapWithMusicAndAlbumAndArtists.fromMap(Map map) {
    return PlaylistMapWithMusicAndAlbumAndArtists(
      playlistMap: PlaylistMap.fromMap(map.getObject('playlist_map')),
      music: MusicWithAlbumAndArtist.fromMap(map.getObject('music')),
    );
  }
}
