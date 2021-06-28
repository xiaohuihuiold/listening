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
) : Parcelable

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
) : Parcelable