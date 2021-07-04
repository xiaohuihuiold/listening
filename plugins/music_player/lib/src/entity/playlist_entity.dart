import 'base_time.dart';
import 'playlist_map_entity.dart';
import '../ext/map_ext.dart';

class Playlist {
  final int id;
  final String? title;
  final String? cover;
  final BaseTime time;

  const Playlist({
    required this.id,
    this.title,
    this.cover,
    required this.time,
  });

  factory Playlist.fromMap(Map map) {
    return Playlist(
      id: map.getInt('id'),
      title: map.getStringOrNull('title'),
      cover: map.getStringOrNull('cover'),
      time: BaseTime.fromMap(map),
    );
  }
}

class PlaylistWithCounts {
  final Playlist playlist;
  final int counts;

  const PlaylistWithCounts({
    required this.playlist,
    required this.counts,
  });

  factory PlaylistWithCounts.fromMap(Map map) {
    return PlaylistWithCounts(
      playlist: Playlist.fromMap(map.getObject('playlist')),
      counts: map.getInt('counts'),
    );
  }
}

class PlaylistWithMusicAndAlbumAndArtists {
  final Playlist playlist;
  final List<PlaylistMapWithMusicAndAlbumAndArtists> playlistMaps;

  const PlaylistWithMusicAndAlbumAndArtists({
    required this.playlist,
    required this.playlistMaps,
  });

  factory PlaylistWithMusicAndAlbumAndArtists.fromMap(Map map) {
    return PlaylistWithMusicAndAlbumAndArtists(
      playlist: Playlist.fromMap(map.getObject('playlist')),
      playlistMaps: map
          .getArray('playlist_maps')
          .map((e) => PlaylistMapWithMusicAndAlbumAndArtists.fromMap(map))
          .toList(),
    );
  }
}
