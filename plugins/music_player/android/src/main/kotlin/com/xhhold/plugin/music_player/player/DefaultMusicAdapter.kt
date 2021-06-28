package com.xhhold.plugin.music_player.player

import android.content.ContentResolver
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import java.lang.Exception

class DefaultMusicAdapter : MusicPlayer.Adapter() {

    private var mediaPlayer: MediaPlayer? = null

    override val isPlaying: Boolean? get() = mediaPlayer?.isPlaying
    override val duration: Long? get() = mediaPlayer?.duration?.toLong()
    override val currentPosition: Long? get() = mediaPlayer?.currentPosition?.toLong()
    override val defaultVolume: Float get() = 1.0f
    override val defaultDuckVolume: Float get() = 0.5f

    companion object {
        const val TAG = "DefaultMusicAdapter"
    }

    override fun prepare(context: Context, uri: Uri) {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(context, uri)
                setOnCompletionListener {
                    playCompleted?.invoke()
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "$what")
                    true
                }
                if (ContentResolver.SCHEME_FILE == uri.scheme || ContentResolver.SCHEME_CONTENT == uri.scheme) {
                    prepare()
                } else {
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e(TAG, "prepare: ${e.message}")
            }
        }
    }

    override fun play() {
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e(TAG, "play: ${e.message}")
        }
    }

    override fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "pause: ${e.message}")
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "stop: ${e.message}")
        }
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun seekTo(position: Long) {
        try {
            mediaPlayer?.seekTo(position.toInt())
        } catch (e: Exception) {
            Log.e(TAG, "seekTo: ${e.message}")
        }
    }

    override fun setVolume(left: Float, right: Float) {
        mediaPlayer?.setVolume(left, right)
    }
}