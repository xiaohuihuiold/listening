package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "artist", indices = [Index(value = ["key"], unique = true)])
data class Artist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "key")
    var key: String,
    @ColumnInfo(name = "title")
    var title: String?,
    @ColumnInfo(name = "cover")
    var cover: String?,
    @Embedded
    var time: BaseTime,
) : Parcelable

@Parcelize
data class ArtistWithCounts(
    @Embedded
    val artist: Artist,
    @ColumnInfo(name = "counts")
    val counts: Int
) : Parcelable

@Parcelize
data class ArtistWithMusicAndAlbums(
    @Embedded
    val artist: Artist?,
    @Relation(
        entity = Music::class,
        parentColumn = "id",
        entityColumn = "artist_id"
    )
    val musics: List<MusicWithAlbumAndArtist>
) : Parcelable
