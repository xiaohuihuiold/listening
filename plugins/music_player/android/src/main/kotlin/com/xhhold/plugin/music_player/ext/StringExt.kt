package com.xhhold.plugin.music_player.ext

import android.content.ContentResolver
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import java.security.MessageDigest

fun String.toUri(): Uri {
    return Uri.parse(this)
}

fun String.toSchemaRoot(): String {
    return "$this:-1"
}

fun String.toParams(): List<String> {
    return split(":")
}

fun String.generateMd5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

fun String.openFileDescriptorRead(contentResolver: ContentResolver): ParcelFileDescriptor? {
    var parcelFileDescriptor: ParcelFileDescriptor? = null
    try {
        parcelFileDescriptor = contentResolver.openFileDescriptor(Uri.parse(this), "r")
    } catch (e: Exception) {
        Log.w("openFileDescriptorRead", "failed:${e.message ?: ""}")
    }
    return parcelFileDescriptor
}