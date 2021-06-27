package com.xhhold.plugin.music_player.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhhold.plugin.music_player.entity.Artist
import com.xhhold.plugin.music_player.entity.ArtistWithCounts
import com.xhhold.plugin.music_player.entity.ArtistWithMusicAndAlbums

@Dao
interface ArtistDao : BaseDao<Artist> {

    @Query("SELECT artist.*,COUNT(artist.id) AS counts FROM artist LEFT JOIN music ON music.artist_id = artist.id GROUP BY artist.id")
    fun getAllArtistWithCounts(): List<ArtistWithCounts>

    @Query("SELECT artist.*,COUNT(artist.id) AS counts FROM artist LEFT JOIN music ON music.artist_id = artist.id GROUP BY artist.id HAVING artist.id = :artistId")
    fun getArtistWithCountsById(artistId: Long): ArtistWithCounts?

    @Transaction
    @Query("SELECT * FROM artist WHERE artist.id = :artistId")
    fun getArtistWithMusicAndAlbumsById(artistId: Long): ArtistWithMusicAndAlbums?

    @Query("SELECT * FROM artist WHERE `key` = :artistKey")
    fun getArtistByKey(artistKey: String): Artist?
}