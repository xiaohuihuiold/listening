package com.xhhold.plugin.music_player.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhhold.plugin.music_player.entity.Playlist
import com.xhhold.plugin.music_player.entity.PlaylistWithCounts
import com.xhhold.plugin.music_player.entity.PlaylistWithMusicAndAlbumAndArtists

@Dao
interface PlaylistDao : BaseDao<Playlist> {

    @Query("SELECT playlist.*,COUNT(playlist.id) AS counts FROM playlist LEFT JOIN playlist_map ON playlist_map.playlist_id = playlist.id GROUP BY playlist.id")
    fun getAllPlaylistWithCounts(): List<PlaylistWithCounts>

    @Transaction
    @Query("SELECT * FROM playlist")
    fun getAllPlaylistWithMusicAndAlbumAndArtists(): List<PlaylistWithMusicAndAlbumAndArtists>

    @Transaction
    @Query("SELECT * FROM playlist WHERE id = :playlistId")
    fun getPlaylistWithMusicAndAlbumAndArtistsById(playlistId: Long): PlaylistWithMusicAndAlbumAndArtists?

    @Query("DELETE FROM playlist_map WHERE music_id = :musicId")
    fun deleteMusicBy(musicId: Long): Int
}