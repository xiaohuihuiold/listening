package com.xhhold.plugin.music_player.dao

import androidx.room.*
import com.xhhold.plugin.music_player.entity.Music
import com.xhhold.plugin.music_player.entity.MusicWithAlbumAndArtist

@Dao
interface MusicDao : BaseDao<Music> {

    @Transaction
    @Query("SELECT * FROM music")
    fun getAllMusicWithAlbumAndArtists(): List<MusicWithAlbumAndArtist>

    @Transaction
    @Query("SELECT * FROM music WHERE is_favorite = 1")
    fun getFavoriteMusicWithAlbumAndArtists(): List<MusicWithAlbumAndArtist>

    @Transaction
    @Query("SELECT * FROM music WHERE id = :musicId")
    fun getMusicWithAlbumAndArtistById(musicId: Long): MusicWithAlbumAndArtist?

    @Transaction
    @Query("SELECT * FROM music WHERE album_id IS NULL")
    fun getUnknownAlbumMusicWithAlbumAndArtists(): List<MusicWithAlbumAndArtist>

    @Transaction
    @Query("SELECT * FROM music WHERE artist_id IS NULL")
    fun getUnknownArtistMusicWithAlbumAndArtists(): List<MusicWithAlbumAndArtist>

    @Query("SELECT * FROM music WHERE source = :source")
    fun getMusicBySource(source: String): Music?

    @Query("UPDATE music SET is_favorite = :isFavorite WHERE id = :musicId")
    fun setFavorite(musicId: Long, isFavorite: Boolean): Int
}