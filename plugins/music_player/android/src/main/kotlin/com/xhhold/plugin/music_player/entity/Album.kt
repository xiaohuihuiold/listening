package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "album", indices = [Index(value = ["key"], unique = true)])
data class Album(
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
data class AlbumWithCounts(
    @Embedded
    val album: Album,
    @ColumnInfo(name = "counts")
    val counts: Int
) : Parcelable

@Parcelize
data class AlbumWithMusicAndArtists(
    @Embedded
    val album: Album?,
    @Relation(
        entity = Music::class,
        parentColumn = "id",
        entityColumn = "album_id"
    )
    val musics: List<MusicWithAlbumAndArtist>
) : Parcelable