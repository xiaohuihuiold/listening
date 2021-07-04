import 'base_time.dart';
import 'music_entity.dart';
import '../ext/map_ext.dart';

class Artist {
  final int id;
  final String key;
  final String? title;
  final String? cover;
  final BaseTime time;

  const Artist({
    required this.id,
    required this.key,
    this.title,
    this.cover,
    required this.time,
  });

  factory Artist.fromMap(Map map) {
    return Artist(
      id: map.getInt('id'),
      key: map.getString('key'),
      title: map.getStringOrNull('title'),
      cover: map.getStringOrNull('cover'),
      time: BaseTime.fromMap(map),
    );
  }
}

class ArtistWithCounts {
  final Artist artist;
  final int counts;

  const ArtistWithCounts({
    required this.artist,
    required this.counts,
  });

  factory ArtistWithCounts.fromMap(Map map) {
    return ArtistWithCounts(
      artist: Artist.fromMap(map.getObject('artist')),
      counts: map.getInt('counts'),
    );
  }
}

class ArtistWithMusicAndAlbums {
  final Artist? artist;
  final List<MusicWithAlbumAndArtist> musics;

  const ArtistWithMusicAndAlbums({
    required this.artist,
    required this.musics,
  });

  factory ArtistWithMusicAndAlbums.fromMap(Map map) {
    return ArtistWithMusicAndAlbums(
      artist: map['artist'] == null
          ? null
          : Artist.fromMap(map.getObject('artist')),
      musics: map
          .getArray('musics')
          .map((e) => MusicWithAlbumAndArtist.fromMap(e))
          .toList(),
    );
  }
}
