package com.xhhold.plugin.music_player.provider

import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Point
import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.DocumentsProvider
import android.support.v4.media.MediaBrowserCompat
import android.webkit.MimeTypeMap
import com.xhhold.plugin.music_player.MusicDatabase
import com.xhhold.plugin.music_player.MusicRepository
import com.xhhold.plugin.music_player.MusicSchema
import com.xhhold.plugin.music_player.R
import com.xhhold.plugin.music_player.ext.toMediaItem
import com.xhhold.plugin.music_player.ext.toParams
import com.xhhold.plugin.music_player.ext.toUri

class MusicDocumentsProvider : DocumentsProvider() {

    private lateinit var musicDatabase: MusicDatabase
    private lateinit var musicRepository: MusicRepository
    private val musicLibrary = MusicLibrary()

    companion object {
        const val ROOT = "root"
        private val DEFAULT_ROOT_PROJECTION = arrayOf<String>(
            DocumentsContract.Root.COLUMN_ROOT_ID,
            DocumentsContract.Root.COLUMN_MIME_TYPES,
            DocumentsContract.Root.COLUMN_ICON,
            DocumentsContract.Root.COLUMN_TITLE,
            DocumentsContract.Root.COLUMN_SUMMARY,
            DocumentsContract.Root.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_FLAGS,
            DocumentsContract.Root.COLUMN_AVAILABLE_BYTES
        )

        private val DEFAULT_DOCUMENT_PROJECTION = arrayOf<String>(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
            DocumentsContract.Document.COLUMN_FLAGS,
            DocumentsContract.Document.COLUMN_SIZE
        )

        private fun resolveRootProjection(projection: Array<String>?): Array<String>? {
            return projection ?: DEFAULT_ROOT_PROJECTION
        }

        private fun resolveDocumentProjection(projection: Array<String>?): Array<String>? {
            return projection ?: DEFAULT_DOCUMENT_PROJECTION
        }
    }

    override fun onCreate(): Boolean {
        context?.apply {
            musicDatabase = MusicDatabase.getInstance(this)
            musicRepository = MusicRepository(musicDatabase)
        }
        return context != null
    }

    override fun queryRoots(projection: Array<String>?): Cursor {
        val result = MatrixCursor(resolveRootProjection(projection))

        result.newRow().apply {
            add(DocumentsContract.Root.COLUMN_ROOT_ID, ROOT)
            add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, ROOT)
            add(
                DocumentsContract.Root.COLUMN_SUMMARY,
                context?.packageManager?.getApplicationLabel(context?.applicationInfo!!)
            )
            add(
                DocumentsContract.Root.COLUMN_TITLE,
                context?.packageManager?.getApplicationLabel(context?.applicationInfo!!)
            )
            add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, 0L)
            add(DocumentsContract.Root.COLUMN_MIME_TYPES, DocumentsContract.Root.MIME_TYPE_ITEM)
            add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, ROOT)
            add(DocumentsContract.Root.COLUMN_ICON, R.drawable.ic_music)
        }

        return result
    }

    override fun queryDocument(documentId: String?, projection: Array<String>?): Cursor {
        val result = MatrixCursor(resolveDocumentProjection(projection))
        result.newRow().apply {
            add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, "musics")
            add(
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                "vnd.android.document/directory"
            )
            add(
                DocumentsContract.Document.COLUMN_FLAGS,
                DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL
            )
            add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, "musics")
            add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, 0L)
            add(DocumentsContract.Document.COLUMN_SIZE, 0)
        }
        return result
    }

    override fun queryChildDocuments(
        parentDocumentId: String?,
        projection: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val result = MatrixCursor(resolveDocumentProjection(projection))
        if (parentDocumentId == "musics") {
            musicLibrary.setRoot(result)
        } else {
            musicLibrary.setMediaItem(result, parentDocumentId!!)
        }
        return result
    }

    private fun createItem(cursor: MatrixCursor, schema: String, title: String) =
        cursor.newRow().apply {
            add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, "$schema:-1")
            add(
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                "vnd.android.document/directory"
            )
            add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, title)
        }

    override fun openDocument(
        documentId: String?,
        mode: String?,
        signal: CancellationSignal?
    ): ParcelFileDescriptor {
        val params = documentId!!.toParams()
        return context!!.contentResolver!!.openFileDescriptor(
            musicRepository.musicDao.getMusicWithAlbumAndArtistById(
                params[1].toLong()
            )?.music?.source?.toUri() ?: Uri.parse(""), "r"
        )!!
    }

    override fun openDocumentThumbnail(
        documentId: String?,
        sizeHint: Point?,
        signal: CancellationSignal?
    ): AssetFileDescriptor {
        val params = documentId?.toParams()
        return when (params?.get(0)) {
            MusicSchema.SCHEMA_ALBUM -> {
                context!!.contentResolver!!.openAssetFileDescriptor(
                    musicRepository.albumDao.getAlbumWithCountsById(
                        params[1].toLong()
                    )?.album?.cover?.toUri() ?: Uri.parse(""), "r"
                )!!
            }
            MusicSchema.SCHEMA_ARTIST -> {
                context!!.contentResolver!!.openAssetFileDescriptor(
                    musicRepository.artistDao.getArtistWithCountsById(
                        params[1].toLong()
                    )?.artist?.cover?.toUri() ?: Uri.parse(""), "r"
                )!!
            }
            MusicSchema.SCHEMA_MUSIC -> {
                context!!.contentResolver!!.openAssetFileDescriptor(
                    musicRepository.musicDao.getMusicWithAlbumAndArtistById(
                        params[1].toLong()
                    )?.album?.cover?.toUri() ?: Uri.parse(""), "r"
                )!!
            }
            else -> context!!.contentResolver!!.openAssetFileDescriptor(Uri.EMPTY, "r")!!
        }
    }


    inner class MusicLibrary {

        fun setRoot(cursor: MatrixCursor) {
            createItem(
                cursor,
                MusicSchema.SCHEMA_FAVORITE_MUSIC,
                context!!.getString(R.string.favorite_music)
            )
            createItem(
                cursor,
                MusicSchema.SCHEMA_ALL_MUSIC,
                context!!.getString(R.string.all_music)
            )
            createItem(
                cursor,
                MusicSchema.SCHEMA_PLAYLIST,
                context!!.getString(R.string.playlist)
            )
            createItem(
                cursor,
                MusicSchema.SCHEMA_NOW_PLAYLIST,
                context!!.getString(R.string.now_playlist)
            )
            createItem(cursor, MusicSchema.SCHEMA_ALBUM, context!!.getString(R.string.album))
            createItem(cursor, MusicSchema.SCHEMA_ARTIST, context!!.getString(R.string.artist))
        }

        fun setMediaItem(result: MatrixCursor, parentId: String) {
            val params = parentId.toParams()
            val schema = params[0]
            val id = params[1].toLong()
            when (schema) {
                MusicSchema.SCHEMA_PLAYLIST -> setPlayList(result, id)
                MusicSchema.SCHEMA_ALBUM -> setAlbum(result, id)
                MusicSchema.SCHEMA_ARTIST -> setArtist(result, id)
                MusicSchema.SCHEMA_ALL_MUSIC -> setAllMusic(result)
                MusicSchema.SCHEMA_FAVORITE_MUSIC -> setFavoriteMusic(result)
                MusicSchema.SCHEMA_NOW_PLAYLIST -> setNowPlaylist(result)
                else -> {
                }
            }
        }

        private fun createFileItem(cursor: MatrixCursor, item: MediaBrowserCompat.MediaItem) {
            cursor.newRow().apply {
                add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, item.mediaId)
                add(
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3")
                )
                add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, item.description.title)
                add(
                    DocumentsContract.Document.COLUMN_FLAGS,
                    DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL
                )
            }
        }

        private fun createFolderItem(cursor: MatrixCursor, item: MediaBrowserCompat.MediaItem) {
            cursor.newRow().apply {
                add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, item.mediaId)
                add(
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    "vnd.android.document/directory"
                )
                add(
                    DocumentsContract.Document.COLUMN_FLAGS,
                    DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL
                )
                add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, item.description.title)
            }
        }

        private fun setPlayList(cursor: MatrixCursor, id: Long) {
            if (id == MusicSchema.SCHEMA_ROOT) {
                val playlist = musicRepository.playlistDao
                    .getAllPlaylistWithCounts()
                    .map { it.toMediaItem() }
                playlist.forEach {
                    createFolderItem(cursor, it)
                }
            } else {
                val musics = musicRepository.playlistDao
                    .getPlaylistWithMusicAndAlbumAndArtistsById(id)
                    ?.playlistMaps
                    ?.map { it.music }
                    ?.map { it.toMediaItem() }
                musics?.forEach {
                    createFileItem(cursor, it)
                }
            }
        }

        private fun setAlbum(cursor: MatrixCursor, id: Long) {
            if (id == MusicSchema.SCHEMA_ROOT) {
                val albums = musicRepository.albumDao
                    .getAllAlbumWithCounts()
                    .map { it.toMediaItem() }
                albums.forEach {
                    createFolderItem(cursor, it)
                }
            } else {
                val musics = musicRepository.albumDao
                    .getAlbumWithMusicAndArtistsById(id)
                    ?.musics
                    ?.map { it.toMediaItem() }
                musics?.forEach {
                    createFileItem(cursor, it)
                }
            }
        }

        private fun setArtist(cursor: MatrixCursor, id: Long) {
            if (id == MusicSchema.SCHEMA_ROOT) {
                val artists = musicRepository.artistDao
                    .getAllArtistWithCounts()
                    .map { it.toMediaItem() }
                artists.forEach {
                    createFolderItem(cursor, it)
                }
            } else {
                val musics = musicRepository.artistDao
                    .getArtistWithMusicAndAlbumsById(id)
                    ?.musics
                    ?.map { it.toMediaItem() }
                musics?.forEach {
                    createFileItem(cursor, it)
                }
            }
        }

        private fun setAllMusic(cursor: MatrixCursor) {
            val musics = musicRepository.musicDao
                .getAllMusicWithAlbumAndArtists()
                .map { it.toMediaItem() }
            musics.forEach {
                createFileItem(cursor, it)
            }
        }

        private fun setFavoriteMusic(cursor: MatrixCursor) {
            val musics = musicRepository.musicDao
                .getFavoriteMusicWithAlbumAndArtists()
                .map { it.toMediaItem() }
            musics.forEach {
                createFileItem(cursor, it)
            }
        }

        private fun setNowPlaylist(cursor: MatrixCursor) {
            val musics = musicRepository.nowPlaylistDao
                .getPlayingMusicWithAlbumAndArtists()
                .map { it.music.toMediaItem() }
            musics.forEach {
                createFileItem(cursor, it)
            }
        }
    }
}