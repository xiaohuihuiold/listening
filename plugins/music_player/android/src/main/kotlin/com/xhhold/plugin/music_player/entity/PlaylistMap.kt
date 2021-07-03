package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "playlist_map")
data class PlaylistMap(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,
    @ColumnInfo(name = "music_id")
    val musicId: Long,
    @Embedded
    var time: BaseTime,
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "playlist_id" to playlistId,
            "music_id" to musicId,
            "add_time" to time.addTime,
            "update_time" to time.updateTime
        )
}

@Parcelize
data class PlaylistMapWithMusicAndAlbumAndArtists(
    @Embedded
    val playlistMap: PlaylistMap,
    @Relation(
        entity = Music::class,
        parentColumn = "music_id",
        entityColumn = "id"
    )
    val music: MusicWithAlbumAndArtist
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "playlist_map" to playlistMap.toMap(),
            "music" to music.toMap()
        )
}