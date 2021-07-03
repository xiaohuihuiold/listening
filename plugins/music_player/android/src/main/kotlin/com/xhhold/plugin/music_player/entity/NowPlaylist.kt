package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "now_playlist")
data class NowPlaylist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "music_id")
    val musicId: Long,
    @Embedded
    val time: BaseTime
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "music_id" to musicId,
            "add_time" to time.addTime,
            "update_time" to time.updateTime
        )
}

@Parcelize
data class NowPlaylistWithMusicAndAlbumAndArtists(
    @Embedded
    val nowPlaylist: NowPlaylist,
    @Relation(
        entity = Music::class,
        parentColumn = "music_id",
        entityColumn = "id"
    )
    val music: MusicWithAlbumAndArtist
) : Parcelable {
    fun toMap(): Map<String, Any?> =
        mapOf(
            "now_playlist" to nowPlaylist.toMap(),
            "music" to music.toMap()
        )
}