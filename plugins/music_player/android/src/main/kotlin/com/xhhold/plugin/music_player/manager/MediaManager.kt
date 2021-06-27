package com.xhhold.plugin.music_player.manager

import android.content.Context
import com.xhhold.plugin.music_player.manager.DataStoreManager
import com.xhhold.plugin.music_player.manager.FileManager

object MediaManager {
    fun init(context: Context) {
        FileManager.init(context)
        DataStoreManager.init(context)
    }
}