package com.xhhold.plugin.music_player.datastore

import androidx.datastore.core.Serializer
import androidx.datastore.preferences.PreferencesMapCompat
import androidx.datastore.preferences.PreferencesProto
import java.io.InputStream
import java.io.OutputStream

data class MusicPlayerData(
    var playlistParentId: String,
    var playlistNowId: Long,
    var playlistNowCurrent: Long,
)

object MusicPlayerStore : Serializer<MusicPlayerData> {
    override val defaultValue: MusicPlayerData
        get() = MusicPlayerData("", -1L, 0L)

    override fun readFrom(input: InputStream): MusicPlayerData {
        val map = PreferencesMapCompat.readFrom(input).preferencesMap
        return MusicPlayerData(
            playlistParentId = map["playlistParentId"]?.string ?: "",
            playlistNowId = map["playlistNowId"]?.long ?: -1L,
            playlistNowCurrent = map["playlistNowCurrent"]?.long ?: 0L
        )
    }

    override fun writeTo(t: MusicPlayerData, output: OutputStream) {
        PreferencesProto.PreferenceMap.newBuilder().apply {
            putPreferences(
                "playlistParentId",
                PreferencesProto.Value.newBuilder().setString(t.playlistParentId).build()
            )
            putPreferences(
                "playlistNowId",
                PreferencesProto.Value.newBuilder().setLong(t.playlistNowId).build()
            )
            putPreferences(
                "playlistNowCurrent",
                PreferencesProto.Value.newBuilder().setLong(t.playlistNowCurrent).build()
            )
        }.build().writeTo(output)
    }
}