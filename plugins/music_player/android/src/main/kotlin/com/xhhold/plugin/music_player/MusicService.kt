package com.xhhold.plugin.music_player

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.xhhold.plugin.music_player.manager.MediaManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * 音乐服务,包含扫描与播放
 */
class MusicService : MediaBrowserServiceCompat(), CoroutineScope {
    lateinit var musicRepository: MusicRepository

    private val coroutineScopeContext = SupervisorJob() + Dispatchers.Main.immediate

    override val coroutineContext: CoroutineContext get() = coroutineScopeContext

    companion object {
        private const val TAG = "MusicService"
    }

    override fun onCreate() {
        super.onCreate()
        MediaManager.init(applicationContext)
        musicRepository = MusicRepository(MusicDatabase.getInstance(this))
    }

    override fun onDestroy() {
        coroutineScopeContext.cancel()
        musicRepository.close()
        super.onDestroy()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }
}