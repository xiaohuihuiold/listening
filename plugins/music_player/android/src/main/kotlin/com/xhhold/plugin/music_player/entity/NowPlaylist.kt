package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

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
) : Parcelable

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
) : Parcelable