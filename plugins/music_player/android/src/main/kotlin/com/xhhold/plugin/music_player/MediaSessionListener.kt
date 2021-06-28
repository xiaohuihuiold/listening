package com.xhhold.plugin.music_player

import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.xhhold.plugin.music_player.ext.toMetadata
import com.xhhold.plugin.music_player.entity.NowPlaylist
import com.xhhold.plugin.music_player.ext.createMediaDescription
import com.xhhold.plugin.music_player.ext.toParams
import com.xhhold.plugin.music_player.manager.DataStoreManager
import com.xhhold.plugin.music_player.player.DefaultMusicAdapter
import com.xhhold.plugin.music_player.player.MusicPlayer
import com.xhhold.plugin.music_player.player.MusicPlayerStateListener
import com.xhhold.plugin.music_player.util.CommonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MediaSessionListener(private val service: MusicService) : MediaSessionCompat.Callback() {

    private var queueIndex: Int = -1
    private var currentId = -1L
    private var parentId: String = ""

    private val playlist = arrayListOf<MediaSessionCompat.QueueItem>()
    private val musicPlayer =
        MusicPlayer(service, DefaultMusicAdapter(), MusicPlayerStateListener(service))

    override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
        when (command) {
            Command.SCAN -> {
                service.launch(Dispatchers.IO) {
                    MusicScanner.scan(service)
                    service.mediaSession.sendSessionEvent(
                        Command.SCAN_CALLBACK,
                        Bundle().apply { putBoolean("success", true) }
                    )
                }
            }
        }
    }

    private fun play(schema: String, id: Long, childId: Long?) {
        service.launch(Dispatchers.IO) {
            service.mediaSession.controller.transportControls.pause()
            playlist.clear()
            currentId = -1
            service.musicRepository.getMusicBySchema(schema, id)
                ?.map {
                    MediaSessionCompat.QueueItem(it.createMediaDescription(), ++currentId)
                }?.apply {
                    playlist.addAll(this)
                }
            queueIndex = playlist.indexOfFirst { it.description.mediaId == childId?.toString() }
            if (queueIndex == -1 && playlist.isNotEmpty()) {
                queueIndex = 0
            }
            if (schema == MusicSchema.SCHEMA_NOW_PLAYLIST) {
                parentId = DataStoreManager.musicPlayerDataStore.data.first().playlistParentId
            } else {
                service.musicRepository.setNowPlaylist(playlist.map { it.description.mediaId!!.toLong() })
                DataStoreManager.musicPlayerDataStore.updateData {
                    it.copy(playlistParentId = parentId, playlistNowCurrent = 0L)
                }
            }
            service.mediaSession.setQueue(playlist)
            if (queueIndex != -1) {
                service.mediaSession.controller.transportControls.play()
            }
            service.mediaSession.sendSessionEvent(Command.REFRESH_NOW_PLAYLIST, null)
        }
    }

    override fun onAddQueueItem(description: MediaDescriptionCompat?) {
        description?.mediaId?.apply {
            if (playlist.indexOfFirst { it.description.mediaId == this } != -1) {
                return@apply
            }
            runBlocking {
                service.musicRepository.nowPlaylistDao.insert(
                    NowPlaylist(0, toLong(), CommonUtil.getTime())
                )
            }
            playlist.add(MediaSessionCompat.QueueItem(description, ++currentId))
            queueIndex = if (queueIndex == -1) 0 else queueIndex
            service.mediaSession.setQueue(playlist)
            service.mediaSession.sendSessionEvent(Command.REFRESH_NOW_PLAYLIST, null)
        }
    }

    override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
        description?.mediaId?.apply {
            runBlocking {
                service.musicRepository.nowPlaylistDao.deleteMusicById(toLong())
            }
            playlist.removeIf { it.description.mediaId == this }
            queueIndex = playlist.indexOfFirst { it.description.mediaId == this }
            service.mediaSession.setQueue(playlist)
            service.mediaSession.sendSessionEvent(Command.REFRESH_NOW_PLAYLIST, null)
        }
    }

    override fun onSkipToPrevious() {
        if (--queueIndex <= -1) {
            queueIndex = playlist.size - 1
        }
        onPlay()
    }

    override fun onSkipToNext() {
        if (++queueIndex >= playlist.size) {
            queueIndex = 0
        }
        onPlay()
    }

    override fun onSkipToQueueItem(id: Long) {
        queueIndex = playlist.indexOfFirst { it.queueId == id }
        onPlay()
    }

    override fun onPrepare() {
        if (queueIndex < 0 || playlist.isEmpty()) {
            val nowPlaylist =
                service.musicRepository.nowPlaylistDao.getPlayingMusicWithAlbumAndArtists()
            if (nowPlaylist.isEmpty()) {
                return
            }
            val queueList = nowPlaylist.map { it.music }.map {
                MediaSessionCompat.QueueItem(
                    it.createMediaDescription(),
                    ++currentId
                )
            }
            playlist.clear()
            playlist.addAll(queueList)
            runBlocking {
                val musicDataStore = DataStoreManager.musicPlayerDataStore.data.first()
                queueIndex =
                    playlist.indexOfFirst { it.description.mediaId == musicDataStore.playlistNowId.toString() }
                parentId = musicDataStore.playlistParentId
                if (queueIndex == -1) {
                    queueIndex = 0
                }
            }
        }
        if (service.currentMetadata?.description?.mediaId == playlist[queueIndex].description.mediaId) {
            return
        }
        service.currentMetadata =
            playlist[queueIndex].description.toMetadata(service.contentResolver)
        service.mediaSession.setMetadata(service.currentMetadata)
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        mediaId?.apply {
            parentId = this
            val id = toParams()
            val child = extras?.getString(Command.PLAY_PARAMS_CHILD_ID)?.toParams()
            play(id[0], id[1].toLong(), child?.get(1)?.toLong())
        }
    }

    override fun onPlay() {
        onPrepare()
        service.currentMetadata?.description?.mediaUri?.apply {
            runBlocking {
                val musicDataStore = DataStoreManager.musicPlayerDataStore.data.first()
                if (parentId == musicDataStore.playlistParentId &&
                    service.currentMetadata?.description?.mediaId == musicDataStore.playlistNowId.toString()
                ) {
                    musicPlayer.seekTo(musicDataStore.playlistNowCurrent)
                } else {
                    musicPlayer.seekTo(0L)
                }
                DataStoreManager.musicPlayerDataStore.updateData {
                    it.copy(
                        playlistNowId = service.currentMetadata?.description?.mediaId?.toLong()
                            ?: -1L
                    )
                }
                musicDataStore.apply { }
            }
            musicPlayer.play(this)
        }
    }

    override fun onPause() {
        musicPlayer.pause()
    }

    override fun onStop() {
        musicPlayer.stop()
    }

    override fun onSeekTo(pos: Long) {
        musicPlayer.seekTo(pos)
    }

    object Command {
        const val PLAY_PARAMS_CHILD_ID = "child_id"

        const val SCAN = "scan"
        const val SCAN_CALLBACK = "scan_callback"

        const val REFRESH_NOW_PLAYLIST = "refresh_now_playlist"
    }
}