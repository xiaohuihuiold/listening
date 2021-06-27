package com.xhhold.plugin.music_player.ext

import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import com.xhhold.plugin.music_player.entity.MusicWithAlbumAndArtist

fun MusicWithAlbumAndArtist.createMediaDescription(schema: String?): MediaDescriptionCompat {
    return MediaDescriptionCompat.Builder().apply {
        setTitle(music.title)
        setSubtitle(artist?.title)
        setDescription(album?.title)
        setMediaUri(music.source.toUri())
        if (schema == null) {
            setMediaId(music.id.toString())
        } else {
            setMediaId("$schema:${music.id}")
        }
        setIconUri((music.cover ?: album?.cover)?.toUri())
        setExtras(Bundle().apply {
            putLong("duration", music.duration)
            putString("album", album?.title ?: "")
            putString("artist", artist?.title ?: "")
            setData(this@createMediaDescription)
        })
    }.build()
}

fun MusicWithAlbumAndArtist.createMediaDescription(): MediaDescriptionCompat {
    return createMediaDescription(null)
}