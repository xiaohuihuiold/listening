package com.xhhold.media.ext

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.xhhold.media.drawable.TextDrawable
import java.lang.Exception

fun Uri.toBitmap(contentResolver: ContentResolver): Bitmap? = toBitmap(contentResolver, null)

fun Uri.toBitmap(contentResolver: ContentResolver, defaultText: String?): Bitmap? {
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