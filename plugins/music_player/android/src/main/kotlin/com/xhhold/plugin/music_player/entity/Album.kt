package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import com.xhhold.plugin.music_player.ext.toPath
import com.xhhold.plugin.music_player.ext.toUri
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
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "key" to key,
            "title" to title,
            "cover" to (cover?.toUri()?.toPath() ?: cover),
            "add_time" to time.addTime,
            "update_time" to time.updateTime
        )
}

@Parcelize
data class AlbumWithCounts(
    @Embedded
    val album: Album,
    @ColumnInfo(name = "counts")
    val counts: Int
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "album" to album.toMap(),
            "counts" to counts
        )
}

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
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "album" to album?.toMap(),
            "musics" to musics.map { it.toMap() }
        )
}