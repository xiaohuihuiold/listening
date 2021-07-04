package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import com.xhhold.plugin.music_player.ext.toPath
import com.xhhold.plugin.music_player.ext.toUri
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "playlist")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
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
            "title" to title,
            "cover" to (cover?.toUri()?.toPath() ?: cover),
            "add_time" to time.addTime,
            "update_time" to time.updateTime
        )
}

@Parcelize
data class PlaylistWithCounts(
    @Embedded
    val playlist: Playlist,
    @ColumnInfo(name = "counts")
    val counts: Int
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "playlist" to playlist.toMap(),
            "counts" to counts
        )
}


@Parcelize
data class PlaylistWithMusicAndAlbumAndArtists(
    @Embedded
    val playlist: Playlist,
    @Relation(
        entity = PlaylistMap::class,
        parentColumn = "id",
        entityColumn = "playlist_id",
    )
    val playlistMaps: List<PlaylistMapWithMusicAndAlbumAndArtists>
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "playlist" to playlist.toMap(),
            "playlist_maps" to playlistMaps.map { it.toMap() }
        )
}