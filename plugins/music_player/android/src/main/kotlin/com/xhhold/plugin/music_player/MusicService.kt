package com.xhhold.plugin.music_player

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.xhhold.plugin.music_player.manager.MediaManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * 音乐服务,包含扫描与播放
 */
class MusicService : MediaBrowserServiceCompat(), CoroutineScope {
    lateinit var mediaSession: MediaSessionCompat
    lateinit var musicRepository: MusicRepository
    private lateinit var mediaSessionListener: MediaSessionListener

    var currentMetadata: MediaMetadataCompat? = null

    private val coroutineScopeContext = SupervisorJob() + Dispatchers.Main.immediate

    override val coroutineContext: CoroutineContext get() = coroutineScopeContext

    companion object {
        private const val TAG = "MusicService"
        private const val BROWSER_ROOT = "root"
    }

    override fun onCreate() {
        super.onCreate()
        MediaManager.init(applicationContext)
        mediaSessionListener = MediaSessionListener(this)
        musicRepository = MusicRepository(MusicDatabase.getInstance(this))
        mediaSession = MediaSessionCompat(this, TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
            setSessionToken(sessionToken)
            setCallback(mediaSessionListener)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

    override fun onDestroy() {
        coroutineScopeContext.cancel()
        mediaSessionListener.onStop()
        mediaSession.isActive = false
        mediaSession.release()
        musicRepository.close()
        super.onDestroy()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(BROWSER_ROOT, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            BROWSER_ROOT -> result.sendResult(musicRepository.getRoot(applicationContext))
            else -> {
                result.detach()
                launch(Dispatchers.IO) {
                    result.sendResult(musicRepository.getMediaItem(parentId))
                }
            }
        }
    }
}