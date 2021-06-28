package com.xhhold.plugin.music_player.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import com.xhhold.plugin.music_player.datastore.MusicPlayerData
import com.xhhold.plugin.music_player.datastore.MusicPlayerStore

object DataStoreManager {

    lateinit var musicPlayerDataStore: DataStore<MusicPlayerData>

    fun init(context: Context) {
        context.apply {
            musicPlayerDataStore = createDataStore(fileName = "music_player", MusicPlayerStore)
        }
    }
}