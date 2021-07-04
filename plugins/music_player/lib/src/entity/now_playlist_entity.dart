import 'base_time.dart';
import 'music_entity.dart';
import '../ext/map_ext.dart';

class NowPlaylist {
  final int id;
  final int musicId;
  final BaseTime time;

  const NowPlaylist({
    required this.id,
    required this.musicId,
    required this.time,
  });

  factory NowPlaylist.fromMap(Map map) {
    return NowPlaylist(
      id: map.getInt('id'),
      musicId: map.getInt('music_id'),
      time: BaseTime.fromMap(map),
    );
  }
}

class NowPlaylistWithMusicAndAlbumAndArtists {
  final NowPlaylist nowPlaylist;
  final MusicWithAlbumAndArtist music;

  const NowPlaylistWithMusicAndAlbumAndArtists({
    required this.nowPlaylist,
    required this.music,
  });

  factory NowPlaylistWithMusicAndAlbumAndArtists.fromMap(Map map) {
    return NowPlaylistWithMusicAndAlbumAndArtists(
      nowPlaylist: NowPlaylist.fromMap(map.getObject('now_playlist')),
      music: MusicWithAlbumAndArtist.fromMap(
        map.getObject('music'),
      ),
    );
  }
}
