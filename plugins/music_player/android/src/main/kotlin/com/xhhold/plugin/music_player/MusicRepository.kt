package com.xhhold.plugin.music_player

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.util.Log
import com.xhhold.plugin.music_player.entity.Music
import com.xhhold.plugin.music_player.entity.MusicWithAlbumAndArtist
import com.xhhold.plugin.music_player.entity.NowPlaylist
import com.xhhold.plugin.music_player.ext.toMediaItem
import com.xhhold.plugin.music_player.ext.toParams
import com.xhhold.plugin.music_player.util.CommonUtil

class MusicRepository(
    private val database: MusicDatabase,
) {
    val albumDao = database.albumDao()
    val artistDao = database.artistDao()
    val musicDao = database.musicDao()
    val playlistDao = database.playlistDao()
    val nowPlaylistDao = database.nowPlaylistDao()

    companion object {
        private const val TAG = "MusicRepository"
    }

    fun close() {
        database.close()
    }

    fun addMusic(musicWithAlbumAndArtist: MusicWithAlbumAndArtist) {
        val music = musicWithAlbumAndArtist.music
        val album = musicWithAlbumAndArtist.album
        val artist = musicWithAlbumAndArtist.artist
        database.runInTransaction {
            if (musicDao.getMusicBySource(music.source) != null) {
                return@runInTransaction
            }
            var albumId: Long? = null
            if (album != null) {
                albumId = albumDao.getAlbumByKey(album.key)?.id
                    ?: albumDao.insert(album).run {
                        if (size > 0) first() else null
                    }
            }
            var artistId: Long? = null
            if (artist != null) {
                artistId = artistDao.getArtistByKey(artist.key)?.id
                    ?: artistDao.insert(artist).run {
                        if (size > 0) first() else null
                    }
            }
            music.let {
                it.albumId = albumId
                it.artistId = artistId
            }
            musicDao.insert(music)
        }
    }

    fun removeMusic(music: Music) {
        database.runInTransaction {
            musicDao.delete(music)
            if (music.albumId != null) {
                val counts = albumDao.getAlbumWithCountsById(music.albumId!!)
                if (counts?.counts == 0) {
                    albumDao.delete(counts.album)
                }
            }
            if (music.artistId != null) {
                val counts = artistDao.getArtistWithCountsById(music.artistId!!)
                if (counts?.counts == 0) {
                    artistDao.delete(counts.artist)
                }
            }
            playlistDao.deleteMusicBy(music.id)
        }
    }

    fun setNowPlaylist(musics: List<Long>) {
        database.runInTransaction {
            nowPlaylistDao.deleteAll()
            nowPlaylistDao.insert(musics.map {
                NowPlaylist(
                    id = 0,
                    musicId = it,
                    time = CommonUtil.getTime()
                )
            })
        }
    }

    fun getRoot(context: Context): MutableList<MediaBrowserCompat.MediaItem> {
        return arrayListOf(
            createRootItem(
                MusicSchema.SCHEMA_FAVORITE_MUSIC,
                context.getString(R.string.favorite_music)
            ),
            createRootItem(MusicSchema.SCHEMA_ALL_MUSIC, context.getString(R.string.all_music)),
            createRootItem(MusicSchema.SCHEMA_PLAYLIST, context.getString(R.string.playlist)),
            createRootItem(
                MusicSchema.SCHEMA_NOW_PLAYLIST,
                context.getString(R.string.now_playlist)
            ),
            createRootItem(MusicSchema.SCHEMA_ALBUM, context.getString(R.string.album)),
            createRootItem(MusicSchema.SCHEMA_ARTIST, context.getString(R.string.artist))
        )
    }

    private fun createRootItem(
        schema: String,
        title: String,
    ): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder().apply {
                setMediaId("$schema:${MusicSchema.SCHEMA_ROOT}")
                setTitle(title)
            }.build(),
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE or MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )
    }

    fun getMusicBySchema(schema: String, id: Long): List<MusicWithAlbumAndArtist>? {
        return when (schema) {
            MusicSchema.SCHEMA_PLAYLIST -> playlistDao.getPlaylistWithMusicAndAlbumAndArtistsById(
                id
            )?.playlistMaps?.map { it.music }
            MusicSchema.SCHEMA_ALBUM -> albumDao.getAlbumWithMusicAndArtistsById(id)?.musics
            MusicSchema.SCHEMA_ARTIST -> artistDao.getArtistWithMusicAndAlbumsById(id)?.musics
            MusicSchema.SCHEMA_NOW_PLAYLIST -> nowPlaylistDao.getPlayingMusicWithAlbumAndArtists()
                .map { it.music }
            MusicSchema.SCHEMA_ALL_MUSIC -> musicDao.getAllMusicWithAlbumAndArtists()
            MusicSchema.SCHEMA_FAVORITE_MUSIC -> musicDao.getFavoriteMusicWithAlbumAndArtists()
            else -> null
        }
    }

    fun getMediaItem(parentId: String): MutableList<MediaBrowserCompat.MediaItem>? {
        val params = parentId.toParams()
        val schema = params[0]
        try {
            val id = params[1].toLong()
            return when (schema) {
                MusicSchema.SCHEMA_MUSIC -> getMusic(id)
                MusicSchema.SCHEMA_PLAYLIST -> getPlayList(id)
                MusicSchema.SCHEMA_ALBUM -> getAlbum(id)
                MusicSchema.SCHEMA_ARTIST -> getArtist(id)
                MusicSchema.SCHEMA_ALL_MUSIC -> getAllMusic()
                MusicSchema.SCHEMA_FAVORITE_MUSIC -> getFavoriteMusic()
                MusicSchema.SCHEMA_NOW_PLAYLIST -> getNowPlaylist()
                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "getMediaItem: ${e.message}")
            return null
        }
    }

    private fun getMusic(id: Long): MutableList<MediaBrowserCompat.MediaItem> {
        val list = arrayListOf<MediaBrowserCompat.MediaItem>()
        val music = musicDao.getMusicWithAlbumAndArtistById(id)
        music?.apply {
            list.add(music.toMediaItem())
        }
        return list
    }

    private fun getPlayList(id: Long): MutableList<MediaBrowserCompat.MediaItem> {
        val list = arrayListOf<MediaBrowserCompat.MediaItem>()
        if (id == MusicSchema.SCHEMA_ROOT) {
            val playlist = playlistDao.getAllPlaylistWithCounts().map { it.toMediaItem() }
            list.addAll(playlist)
        } else {
            val musics = playlistDao.getPlaylistWithMusicAndAlbumAndArtistsById(id)?.playlistMaps
                ?.map { it.music }
                ?.map { it.toMediaItem() }
            musics?.apply {
                list.addAll(this)
            }
        }
        return list
    }

    private fun getAlbum(id: Long): MutableList<MediaBrowserCompat.MediaItem> {
        val list = arrayListOf<MediaBrowserCompat.MediaItem>()
        if (id == MusicSchema.SCHEMA_ROOT) {
            val albums = albumDao.getAllAlbumWithCounts().map { it.toMediaItem() }
            list.addAll(albums)
        } else {
            val musics = albumDao.getAlbumWithMusicAndArtistsById(id)?.musics
                ?.map { it.toMediaItem() }
            musics?.apply {
                list.addAll(this)
            }
        }
        return list
    }

    private fun getArtist(id: Long): MutableList<MediaBrowserCompat.MediaItem> {
        val list = arrayListOf<MediaBrowserCompat.MediaItem>()
        if (id == MusicSchema.SCHEMA_ROOT) {
            val artists = artistDao.getAllArtistWithCounts().map { it.toMediaItem() }
            list.addAll(artists)
        } else {
            val musics =
                artistDao.getArtistWithMusicAndAlbumsById(id)?.musics?.map { it.toMediaItem() }
            musics?.apply {
                list.addAll(this)
            }
        }
        return list
    }

    private fun getAllMusic(): MutableList<MediaBrowserCompat.MediaItem> {
        val list = arrayListOf<MediaBrowserCompat.MediaItem>()
        val musics = musicDao.getAllMusicWithAlbumAndArtists().map { it.toMediaItem() }
        list.addAll(musics)
        return list
    }

    private fun getFavoriteMusic(): MutableList<MediaBrowserCompat.MediaItem> {
        val list = arrayListOf<MediaBrowserCompat.MediaItem>()
        val musics = musicDao.getFavoriteMusicWithAlbumAndArtists().map { it.toMediaItem() }
        list.addAll(musics)
        return list
    }

    private fun getNowPlaylist(): MutableList<MediaBrowserCompat.MediaItem> {
        val list = arrayListOf<MediaBrowserCompat.MediaItem>()
        val musics =
            nowPlaylistDao.getPlayingMusicWithAlbumAndArtists().map { it.music.toMediaItem() }
        list.addAll(musics)
        return list
    }
}