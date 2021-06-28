package com.xhhold.plugin.music_player.player

import android.net.Uri

interface MusicPlayerController {
    val isPlaying: Boolean?
    val duration: Long?
    val currentPosition: Long?
    val defaultVolume: Float
    val defaultDuckVolume: Float
    fun prepare(uri: Uri) {}
    fun play(uri: Uri) {}
    fun play()
    fun pause()
    fun stop()
    fun release() {}
    fun seekTo(position: Long)
    fun setVolume(left: Float, right: Float)
    fun setVolume(volume: Float) {
        setVolume(volume, volume)
    }
}