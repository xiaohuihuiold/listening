package com.xhhold.plugin.music_player.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.xhhold.plugin.music_player.ext.toPath
import com.xhhold.plugin.music_player.manager.FileManager
import java.io.File
import java.lang.Exception

class MusicProvider : ContentProvider() {

    companion object {
        const val TAG = "MusicProvider"
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val path = uri.toPath() ?: return null
        return try {
            ParcelFileDescriptor.open(
                File(path), ParcelFileDescriptor.MODE_READ_ONLY
            )
        } catch (e: Exception) {
            Log.e(TAG, "openFile field: ${e.message}")
            null
        }
    }
}