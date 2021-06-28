package com.xhhold.plugin.music_player.player

import android.content.Intent
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import com.xhhold.plugin.music_player.MediaNotificationManager
import com.xhhold.plugin.music_player.MusicService
import com.xhhold.plugin.music_player.manager.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicPlayerStateListener(private val service: MusicService) :
    MusicPlayer.OnPlayStateListener {

    private var firstStart = true
    private val mediaNotificationManager = MediaNotificationManager(service)

    override fun onPlayCompleted() {
        service.mediaSession.controller.transportControls.skipToNext()
    }

    override fun onPlayStateChanged(state: PlaybackStateCompat) {
        service.mediaSession.setPlaybackState(state)
        when (state.state) {
            PlaybackStateCompat.STATE_PLAYING -> onPlaying(state)
            PlaybackStateCompat.STATE_PAUSED -> onPaused(state)
            PlaybackStateCompat.STATE_STOPPED -> onStopped(state)
            else -> Log.i("MusicPlayerStateListener", "state: ${state.state}")
        }
    }

    private fun onPlaying(state: PlaybackStateCompat) {
        service.currentMetadata?.apply {
            service.mediaSession.isActive = true
            mediaNotificationManager.start(description, state, service.sessionToken!!)
        }
        if (firstStart) {
            ContextCompat.startForegroundService(service, Intent(service, MusicService::class.java))
            firstStart = false
        }
    }

    private fun onPaused(state: PlaybackStateCompat) {
        savePlayState(state.position)
        service.mediaSession.isActive = false
        service.currentMetadata?.apply {
            mediaNotificationManager.update(description, state, service.sessionToken!!)
        }
    }

    private fun onStopped(state: PlaybackStateCompat) {
        savePlayState(state.position)
        firstStart = true
        service.mediaSession.isActive = false
        mediaNotificationManager.stop(true)
        service.stopSelf()
    }

    private fun savePlayState(position: Long?) {
        service.launch(Dispatchers.IO) {
            DataStoreManager.musicPlayerDataStore.updateData {
                it.copy(
                    playlistNowId = service.currentMetadata?.description?.mediaId?.toLong() ?: -1L,
                    playlistNowCurrent = position ?: 0L
                )
            }
        }
    }
}