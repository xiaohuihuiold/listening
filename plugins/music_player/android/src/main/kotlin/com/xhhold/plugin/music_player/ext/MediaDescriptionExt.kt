package com.xhhold.media.ext

import android.content.ContentResolver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat

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
        putBitmap(
            MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
            iconUri?.toBitmap(contentResolver, title?.toString() ?: subtitle?.toString() ?: "")
        )
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