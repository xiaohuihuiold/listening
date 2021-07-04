package com.xhhold.plugin.music_player.ext

import android.content.ContentResolver
import android.content.UriMatcher
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.xhhold.plugin.music_player.drawable.TextDrawable
import com.xhhold.plugin.music_player.manager.FileManager
import com.xhhold.plugin.music_player.provider.MusicProvider
import java.io.File
import java.lang.Exception

fun Uri.toPath(): String? {
    val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(FileManager.musicProviderAuthority, "${FileManager.ALBUM_IMAGE_CACHE_DIR}/*", 1)
        addURI(FileManager.musicProviderAuthority, "${FileManager.IMAGE_CACHE_DIR}/*", 2)
    }
    val result = matcher.match(this)
    if (result == -1 || pathSegments.size < 2) {
        return null
    }
    val path = pathSegments.subList(1, pathSegments.size).joinToString("/")
    val basePath: File?
    when (pathSegments[0]) {
        FileManager.CACHE -> {
            basePath = FileManager.externalCacheDir
        }
        else -> {
            return null
        }
    }
    return "${basePath?.path}/$path"

}

fun Uri.toBitmap(contentResolver: ContentResolver, defaultText: String? = null): Bitmap? {
    val bitmap: Bitmap?
    val parcelFileDescriptor = toString().openFileDescriptorRead(contentResolver)
    if (parcelFileDescriptor != null) {
        bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.fileDescriptor)
        try {
            parcelFileDescriptor.close()
        } catch (e: Exception) {
        }
        return bitmap
    }
    if (defaultText == null) {
        return null
    }
    val drawable = TextDrawable(defaultText)
    bitmap = drawable.toBitmap(512, 512)
    return bitmap

}

fun Uri.openFileDescriptorRead(contentResolver: ContentResolver): ParcelFileDescriptor? {
    var parcelFileDescriptor: ParcelFileDescriptor? = null
    try {
        parcelFileDescriptor = contentResolver.openFileDescriptor(this, "r")
    } catch (e: Exception) {
        Log.w("openFileDescriptorRead", "failed:${e.message ?: ""}")
    }
    return parcelFileDescriptor
}