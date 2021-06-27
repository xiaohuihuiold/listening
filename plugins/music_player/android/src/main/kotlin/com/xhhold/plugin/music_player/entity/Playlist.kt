package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

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
) : Parcelable

@Parcelize
data class PlaylistWithCounts(
    @Embedded
    val playlist: Playlist,
    @ColumnInfo(name = "counts")
    val counts: Int
) : Parcelable

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
) : Parcelable