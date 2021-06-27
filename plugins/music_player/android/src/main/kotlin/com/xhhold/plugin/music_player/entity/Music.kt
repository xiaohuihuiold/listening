package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "music", indices = [Index(value = ["source"], unique = true)])
data class Music(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "title")
    var title: String?,
    @ColumnInfo(name = "album_id")
    var albumId: Long?,
    @ColumnInfo(name = "artist_id")
    var artistId: Long?,
    @ColumnInfo(name = "cover")
    var cover: String?,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "size")
    val size: Long,
    @ColumnInfo(name = "duration")
    val duration: Long,
    @ColumnInfo(name = "sample_rate")
    val sampleRate: Long?,
    @ColumnInfo(name = "channels")
    val channels: Int?,
    @ColumnInfo(name = "bits")
    val bits: Int?,
    @ColumnInfo(name = "bitrate")
    val bitrate: Long?,
    @ColumnInfo(name = "track_number")
    val trackNumber: Int?,
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean,
    @Embedded
    var time: BaseTime,
) : Parcelable

@Parcelize
data class MusicWithAlbumAndArtist(
    @Embedded
    val music: Music,
    @Relation(
        parentColumn = "album_id",
        entityColumn = "id"
    )
    val album: Album?,
    @Relation(
        parentColumn = "artist_id",
        entityColumn = "id"
    )
    val artist: Artist?
) : Parcelable