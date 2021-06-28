package com.xhhold.plugin.music_player.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat

class MusicPlayer(
    private val context: Context,
    private val adapter: Adapter,
    private val playStateListener: OnPlayStateListener
) : MusicPlayerController {
    val currentState get() = playState

    private var playCompleted: Boolean = true
    private var currentUri: Uri? = null
    private var currentStatePosition: Long = 0L
    private var playState: Int = PlaybackStateCompat.STATE_STOPPED
    private var focusCanPlay = true
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val audioFocusManager = AudioFocusManager()

    override val isPlaying: Boolean? get() = adapter.isPlaying
    override val duration: Long? get() = adapter.duration
    override val currentPosition: Long? get() = adapter.currentPosition
    override val defaultVolume: Float get() = adapter.defaultVolume
    override val defaultDuckVolume: Float get() = adapter.defaultDuckVolume

    init {
        adapter.playCompleted = this::onPlayCompleted
    }

    private fun onPlayCompleted() {
        playCompleted = true
        currentStatePosition = 0L
        setNewState(PlaybackStateCompat.STATE_PAUSED)
        playStateListener.onPlayCompleted()
    }

    override fun prepare(uri: Uri) {
        release()
        adapter.prepare(context, uri)
    }

    override fun play(uri: Uri) {
        if (uri != currentUri || isPlaying == null) {
            currentUri = uri
            prepare(uri)
        }
        playCompleted = false
        play()
    }

    override fun play() {
        focusCanPlay = true
        if (audioFocusManager.requestFocus() && isPlaying != null && !playCompleted) {
            adapter.play()
            adapter.seekTo(currentStatePosition)
            currentStatePosition = currentPosition ?: 0L
            setNewState(PlaybackStateCompat.STATE_PLAYING)
        } else {
            stop()
        }
    }

    override fun pause() {
        currentStatePosition = currentPosition ?: 0L
        focusCanPlay = false
        adapter.pause()
        setNewState(PlaybackStateCompat.STATE_PAUSED)
    }

    override fun stop() {
        currentStatePosition = currentPosition ?: 0L
        release()
        audioFocusManager.abandonFocus()
        setNewState(PlaybackStateCompat.STATE_STOPPED)
    }

    override fun release() {
        adapter.stop()
        adapter.release()
    }

    override fun seekTo(position: Long) {
        currentStatePosition = position
        adapter.seekTo(position)
        setNewState(playState)
    }

    override fun setVolume(left: Float, right: Float) = adapter.setVolume(left, right)

    private fun setNewState(state: Int) {
        playState = state
        playCompleted = state == PlaybackStateCompat.STATE_STOPPED
        val stateBuilder = PlaybackStateCompat.Builder().apply {
            setActions(getAvailableActions())
            setState(state, currentStatePosition, 1.0f, SystemClock.elapsedRealtime())
        }
        playStateListener.onPlayStateChanged(stateBuilder.build())
    }

    private fun getAvailableActions(): Long {
        var actions = (PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        actions = when (playState) {
            PlaybackStateCompat.STATE_STOPPED -> actions or
                    (PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PAUSE)
            PlaybackStateCompat.STATE_PLAYING -> actions or
                    (PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SEEK_TO)
            PlaybackStateCompat.STATE_PAUSED -> actions or (
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_SEEK_TO)
            else -> actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE)
        }
        return actions
    }


    abstract class Adapter : MusicPlayerController {
        var playCompleted: (() -> Unit)? = null
        abstract fun prepare(context: Context, uri: Uri)
    }

    interface OnPlayStateListener {
        fun onPlayStateChanged(state: PlaybackStateCompat)
        fun onPlayCompleted()
    }

    private inner class AudioFocusManager : AudioManager.OnAudioFocusChangeListener {

        private val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
            setAudioAttributes(
                AudioAttributes.Builder().apply {
                    setLegacyStreamType(AudioManager.STREAM_MUSIC)
                }.build()
            )
            setOnAudioFocusChangeListener(this@AudioFocusManager)
        }.build()

        fun requestFocus(): Boolean {
            return audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }

        fun abandonFocus() {
            audioManager.abandonAudioFocusRequest(request)
        }

        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    setVolume(defaultVolume)
                    if (focusCanPlay) {
                        play()
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    setVolume(defaultDuckVolume)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (isPlaying == true) {
                        pause()
                        focusCanPlay = true
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    stop()
                }
            }
        }

    }
}