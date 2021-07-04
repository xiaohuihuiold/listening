package com.xhhold.plugin.music_player.ext

import android.content.ContentResolver
import android.graphics.Bitmap
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.xhhold.plugin.music_player.drawable.TextDrawable

fun MediaDescriptionCompat.getDuration(): Long? {
    return extras?.getLong("duration")
}

fun MediaDescriptionCompat.getAlbum(): String? {
    return extras?.getString("album")
}

fun MediaDescriptionCompat.getArtist(): String? {
    return extras?.getString("artist")
}

fun MediaDescriptionCompat.toMetadata(
    contentResolver: ContentResolver
): MediaMetadataCompat {
    val builder = MediaMetadataCompat.Builder()
    builder.apply {
        val icon = iconUri
        val text = title?.toString() ?: subtitle?.toString() ?: ""
        val bitmap = if (icon != null) {
            icon.toBitmap(contentResolver, text)
        } else {
            val drawable = TextDrawable(text)
            drawable.toBitmap(512, 512)
        }
        putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, iconUri?.toString())
        putLong(MediaMetadataCompat.METADATA_KEY_DURATION, getDuration() ?: 0L)
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM, getAlbum())
        putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getArtist())
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaUri.toString())
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, title.toString())
    }
    return builder.build()
}