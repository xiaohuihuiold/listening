import 'base_time.dart';
import 'album_entity.dart';
import 'artist_entity.dart';
import '../ext/map_ext.dart';

class Music {
  final int id;
  final String? title;
  final int? albumId;
  final int? artistId;
  final String? cover;
  final String source;
  final int size;
  final int duration;
  final int? sampleRate;
  final int? channels;
  final int? bits;
  final int? bitrate;
  final int? trackNumber;
  final bool isFavorite;
  final BaseTime time;

  /// new
  final String durationStr;

  const Music({
    required this.id,
    this.title,
    this.albumId,
    this.artistId,
    this.cover,
    required this.source,
    required this.size,
    required this.duration,
    this.sampleRate,
    this.channels,
    this.bits,
    this.bitrate,
    this.trackNumber,
    required this.isFavorite,
    required this.time,
    required this.durationStr,
  });

  factory Music.fromMap(Map map) {
    return Music(
      id: map.getInt('id'),
      title: map.getStringOrNull('title'),
      albumId: map.getIntOrNull('album_id'),
      artistId: map.getIntOrNull('artist_id'),
      cover: map.getStringOrNull('cover'),
      source: map.getString('source'),
      size: map.getInt('size'),
      duration: map.getInt('duration'),
      sampleRate: map.getIntOrNull('sample_rate'),
      channels: map.getIntOrNull('channels'),
      bits: map.getIntOrNull('bits'),
      bitrate: map.getIntOrNull('bitrate'),
      trackNumber: map.getIntOrNull('track_number'),
      isFavorite: map.getBool('is_favorite'),
      time: BaseTime.fromMap(map),
      durationStr: Duration(milliseconds: map.getInt('duration'))
          .toString()
          .replaceAll(RegExp(r'.\d*$'), ''),
    );
  }
}

class MusicWithAlbumAndArtist {
  final Music music;
  final Album? album;
  final Artist? artist;

  const MusicWithAlbumAndArtist({
    required this.music,
    this.album,
    this.artist,
  });

  factory MusicWithAlbumAndArtist.fromMap(Map map) {
    return MusicWithAlbumAndArtist(
      music: Music.fromMap(map.getObject('music')),
      album:
          map['album'] == null ? null : Album.fromMap(map.getObject('album')),
      artist: map['artist'] == null
          ? null
          : Artist.fromMap(map.getObject('artist')),
    );
  }
}
