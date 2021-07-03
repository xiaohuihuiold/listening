package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

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
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "title" to title,
            "album_id" to albumId,
            "artist_id" to artistId,
            "cover" to cover,
            "source" to source,
            "size" to size,
            "duration" to duration,
            "sample_rate" to sampleRate,
            "channels" to channels,
            "bits" to bits,
            "bitrate" to bitrate,
            "track_number" to trackNumber,
            "is_favorite" to isFavorite,
            "add_time" to time.addTime,
            "update_time" to time.updateTime
        )
}

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
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "music" to music.toMap(),
            "album" to album?.toMap(),
            "artist" to artist?.toMap()
        )
}