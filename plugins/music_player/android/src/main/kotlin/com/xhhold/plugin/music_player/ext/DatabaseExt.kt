package com.xhhold.plugin.music_player.ext

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.xhhold.plugin.music_player.MusicSchema
import com.xhhold.plugin.music_player.entity.AlbumWithCounts
import com.xhhold.plugin.music_player.entity.ArtistWithCounts
import com.xhhold.plugin.music_player.entity.MusicWithAlbumAndArtist
import com.xhhold.plugin.music_player.entity.PlaylistWithCounts

/**
 * 返回给客户端用
 */

fun MusicWithAlbumAndArtist.getSchemaId() = "${MusicSchema.SCHEMA_MUSIC}:${music.id}"
fun MusicWithAlbumAndArtist.toMediaItem(): MediaBrowserCompat.MediaItem {
    return MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder().apply {
            setMediaId("${MusicSchema.SCHEMA_MUSIC}:${music.id}")
            setTitle(music.title)
            setSubtitle(artist?.title ?: album?.title)
            setIconUri((music.cover ?: album?.cover ?: artist?.cover)?.toUri())
            setMediaUri(music.source.toUri())
            setExtras(Bundle().setData(this@toMediaItem))
        }.build(),
        MediaBrowserCompat.MediaItem.FLAG_BROWSABLE or MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    )
}

fun PlaylistWithCounts.getSchemaId() = "${MusicSchema.SCHEMA_PLAYLIST}:${playlist.id}"
fun PlaylistWithCounts.toMediaItem(): MediaBrowserCompat.MediaItem {
    return MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder().apply {
            setMediaId("${MusicSchema.SCHEMA_PLAYLIST}:${playlist.id}")
            setTitle(playlist.title)
            setIconUri(playlist.cover?.toUri())
            setExtras(Bundle().setData(this@toMediaItem))
        }.build(),
        MediaBrowserCompat.MediaItem.FLAG_BROWSABLE or MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    )
}

fun AlbumWithCounts.getSchemaId() = "${MusicSchema.SCHEMA_ALBUM}:${album.id}"
fun AlbumWithCounts.toMediaItem(): MediaBrowserCompat.MediaItem {
    return MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder().apply {
            setMediaId("${MusicSchema.SCHEMA_ALBUM}:${album.id}")
            setTitle(album.title)
            setIconUri(album.cover?.toUri())
            setExtras(Bundle().setData(this@toMediaItem))
        }.build(),
        MediaBrowserCompat.MediaItem.FLAG_BROWSABLE or MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    )
}

fun ArtistWithCounts.getSchemaId() = "${MusicSchema.SCHEMA_ARTIST}:${artist.id}"
fun ArtistWithCounts.toMediaItem(): MediaBrowserCompat.MediaItem {
    return MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder().apply {
            setMediaId("${MusicSchema.SCHEMA_ARTIST}:${artist.id}")
            setTitle(artist.title)
            setIconUri(artist.cover?.toUri())
            setExtras(Bundle().setData(this@toMediaItem))
        }.build(),
        MediaBrowserCompat.MediaItem.FLAG_BROWSABLE or MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    )
}