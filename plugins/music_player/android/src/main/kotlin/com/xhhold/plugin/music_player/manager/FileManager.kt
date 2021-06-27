package com.xhhold.plugin.music_player.manager

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object FileManager {
    const val CACHE = "cache"
    const val IMAGE_DIR = "images"
    const val IMAGE_CACHE_DIR = "$CACHE/$IMAGE_DIR"
    const val ALBUM_IMAGE_DIR = "$IMAGE_DIR/albums"
    const val ALBUM_IMAGE_CACHE_DIR = "$CACHE/$ALBUM_IMAGE_DIR"

    var externalCacheDir: File? = null
    var packageName: String? = null
    val musicProviderAuthority get() = "$packageName.MusicProvider"

    private val imageCacheDir: File?
        get() = externalCacheDir?.run {
            getNewDirectory("$path/$IMAGE_DIR")
        }

    private val albumImageCacheDir: File?
        get() = externalCacheDir?.run {
            getNewDirectory("$path/$ALBUM_IMAGE_DIR")
        }

    fun init(context: Context) {
        context.apply {
            FileManager.externalCacheDir = externalCacheDir
            FileManager.packageName = packageName
        }
    }

    private fun getNewDirectory(path: String): File? {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return if (file.exists()) file else null
    }

    fun createContentUri(basePath: String, path: String, name: String): Uri {
        return createContentUri("$basePath/$path", name)
    }

    fun createContentUri(path: String, name: String): Uri {
        return Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
            .authority(musicProviderAuthority)
            .appendEncodedPath(path)
            .appendEncodedPath(name)
            .build()
    }

    fun saveAlbumImageCache(name: String, bytes: ByteArray): Uri? =
        saveAlbumImageCache(name, bytes, false)

    fun saveAlbumImageCache(name: String, bytes: ByteArray, isReplace: Boolean): Uri? {
        if (albumImageCacheDir == null) {
            return null
        }
        val uri = createContentUri(ALBUM_IMAGE_CACHE_DIR, name)
        val path = File("${albumImageCacheDir!!.path}/$name")
        if (!isReplace && path.exists()) {
            return uri
        }
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(path)
            outputStream.write(bytes)
        } catch (e: Exception) {
        } finally {
            outputStream?.close()
        }
        return if (path.exists()) uri else null
    }
}