package com.xhhold.plugin.music_player

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.*
import android.util.Log
import com.xhhold.plugin.music_player.ext.size
import com.xhhold.plugin.music_player.entity.Album
import com.xhhold.plugin.music_player.entity.Artist
import com.xhhold.plugin.music_player.entity.Music
import com.xhhold.plugin.music_player.entity.MusicWithAlbumAndArtist
import com.xhhold.plugin.music_player.ext.generateMd5
import com.xhhold.plugin.music_player.util.CommonUtil
import java.io.FileDescriptor

object MusicFactory {
    private const val TAG = "MusicFactory"

    suspend fun decodeFromPath(
        source: String,
        fileDescriptor: FileDescriptor,
    ): MusicWithAlbumAndArtist? = decodeFromPath(source, fileDescriptor, null)

    suspend fun decodeFromPath(
        source: String,
        fileDescriptor: FileDescriptor,
        saveAlbum: ((md5: String, bytes: ByteArray?) -> String?)?
    ): MusicWithAlbumAndArtist? {
        var music: Music? = null
        var album: Album? = null
        var artist: Artist? = null

        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(fileDescriptor)
            music = createMusic(mediaMetadataRetriever, source, fileDescriptor)
            album = createAlbum(mediaMetadataRetriever, saveAlbum)
            artist = createArtist(mediaMetadataRetriever)
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "无")
        } finally {
            mediaMetadataRetriever.release()
        }
        if (music == null) {
            return null
        }
        return MusicWithAlbumAndArtist(music, album, artist)
    }

    private fun createAlbum(
        mediaMetadataRetriever: MediaMetadataRetriever,
        saveAlbum: ((md5: String, bytes: ByteArray?) -> String?)?
    ): Album? =
        mediaMetadataRetriever.run {
            val title = extractMetadata(METADATA_KEY_ALBUM) ?: return null
            val artist = extractMetadata(METADATA_KEY_ALBUMARTIST) ?: ""
            val md5 = "$title$artist".generateMd5()
            var cover: String? = null
            if (saveAlbum != null) {
                cover = saveAlbum(md5, embeddedPicture)
                if (cover != null) {
                    cover = cover
                }
            }
            Album(
                id = 0,
                key = md5,
                title = title,
                cover = cover,
                time = CommonUtil.getTime()
            )
        }

    private fun createArtist(mediaMetadataRetriever: MediaMetadataRetriever): Artist? =
        mediaMetadataRetriever.run {
            val title = extractMetadata(METADATA_KEY_ARTIST) ?: return null
            Artist(
                id = 0,
                key = title.generateMd5(),
                title = title,
                cover = null,
                time = CommonUtil.getTime()
            )
        }

    private fun createMusic(
        mediaMetadataRetriever: MediaMetadataRetriever,
        source: String,
        fileDescriptor: FileDescriptor,
    ): Music? = mediaMetadataRetriever.run {
        val title = extractMetadata(METADATA_KEY_TITLE)
        Music(
            id = 0,
            title = title,
            albumId = null,
            artistId = null,
            cover = null,
            source = source,
            size = fileDescriptor.size(),
            duration = extractMetadata(METADATA_KEY_DURATION)?.toLong() ?: 0,
            sampleRate = extractMetadata(38)?.toLong(),
            channels = null,
            bits = extractMetadata(39)?.toInt(),
            bitrate = extractMetadata(METADATA_KEY_BITRATE)?.toLong(),
            trackNumber = extractMetadata(METADATA_KEY_CD_TRACK_NUMBER)?.toInt() ?: 0,
            isFavorite = false,
            time = CommonUtil.getTime()
        )
    }
}