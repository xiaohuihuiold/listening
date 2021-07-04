import 'base_time.dart';
import 'music_entity.dart';
import '../ext/map_ext.dart';

class Album {
  final int id;
  final String key;
  final String? title;
  final String? cover;
  final BaseTime time;

  const Album({
    required this.id,
    required this.key,
    this.title,
    this.cover,
    required this.time,
  });

  factory Album.fromMap(Map map) {
    return Album(
      id: map.getInt('id'),
      key: map.getString('key'),
      title: map.getStringOrNull('title'),
      cover: map.getStringOrNull('cover'),
      time: BaseTime.fromMap(map),
    );
  }
}

class AlbumWithCounts {
  final Album album;
  final int counts;

  const AlbumWithCounts({
    required this.album,
    required this.counts,
  });

  factory AlbumWithCounts.fromMap(Map map) {
    return AlbumWithCounts(
      album: Album.fromMap(map.getObject('album')),
      counts: map.getInt('counts'),
    );
  }
}

class AlbumWithMusicAndArtists {
  final Album? album;
  final List<MusicWithAlbumAndArtist> musics;

  const AlbumWithMusicAndArtists({
    required this.album,
    required this.musics,
  });

  factory AlbumWithMusicAndArtists.fromMap(Map map) {
    return AlbumWithMusicAndArtists(
      album:
          map['album'] == null ? null : Album.fromMap(map.getObject('album')),
      musics: map
          .getArray('musics')
          .map((e) => MusicWithAlbumAndArtist.fromMap(e))
          .toList(),
    );
  }
}
