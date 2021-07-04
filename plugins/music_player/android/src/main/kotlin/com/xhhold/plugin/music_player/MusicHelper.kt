package com.xhhold.plugin.music_player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.xhhold.plugin.music_player.entity.*
import com.xhhold.plugin.music_player.ext.currentPlayBackPosition
import com.xhhold.plugin.music_player.ext.getData
import com.xhhold.plugin.music_player.ext.toSchemaRoot
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.*
import kotlin.concurrent.timer

class MusicHelper(private val context: Context) {
    val connected = MutableLiveData<Boolean>()
    val playState = MutableLiveData<PlaybackStateCompat?>()
    val currentMusic = MutableLiveData<MusicWithAlbumAndArtist?>()
    val nowPlaylist = MutableLiveData<List<MusicWithAlbumAndArtist>>()

    private var currentMetadata: MediaMetadataCompat? = null
    private var mediaController: MediaControllerCompat? = null
    private val mediaBrowser = MediaBrowserCompat(
        context, ComponentName(context, MusicService::class.java), ConnectionCallback(), null
    )

    companion object {
        const val TAG = "MusicHelper"
    }

    fun connect() {
        Log.i(TAG, "connect")
        if (connected.value != true) {
            mediaBrowser.connect()
        }
    }

    fun disconnect() {
        Log.i(TAG, "disconnect")
        if (connected.value == true) {
            mediaBrowser.disconnect()
        }
    }

    fun scan() {
        mediaController?.sendCommand(MediaSessionListener.Command.SCAN, null, null)
    }

    fun play() {
        mediaController?.transportControls?.play()
    }

    fun playFromId(parentId: String) {
        playFromId(parentId, null)
    }

    fun playFromId(parentId: String, childId: String?) {
        mediaController?.transportControls?.playFromMediaId(
            parentId,
            Bundle().apply {
                putString(
                    MediaSessionListener.Command.PLAY_PARAMS_CHILD_ID,
                    childId
                )
            },
        )
    }

    fun pause() {
        mediaController?.transportControls?.pause()
    }

    fun stop() {
        mediaController?.transportControls?.stop()
    }

    fun skipToPrevious() {
        mediaController?.transportControls?.skipToPrevious()
    }

    fun skipToNext() {
        mediaController?.transportControls?.skipToNext()
    }

    fun seekTo(duration: Long) {
        mediaController?.transportControls?.seekTo(duration)
    }

    fun refreshConnected() {
        connected.value = mediaBrowser.isConnected
    }

    fun refreshPlayState() {
        playState.value = mediaController?.playbackState
    }

    fun refreshState() {
        refreshConnected()
        refreshPlayState()
        refreshMusic()
        refreshNowPlaylist()
    }

    fun refreshMusic() {
        subscribe("${MusicSchema.SCHEMA_MUSIC}:${currentMetadata?.description?.mediaId}") {
            if (it.isEmpty()) {
                currentMusic.value = null
            } else {
                currentMusic.value =
                    it.first().description.extras!!.getData<MusicWithAlbumAndArtist>()
            }
        }
    }

    fun refreshNowPlaylist() {
        subscribe(MusicSchema.SCHEMA_NOW_PLAYLIST.toSchemaRoot()) { list ->
            nowPlaylist.value =
                list.map { it.description.extras!!.getData<MusicWithAlbumAndArtist>()!! }
        }
    }

    fun getPlaylist(call: MethodCall, result: MethodChannel.Result) {
        subscribe(MusicSchema.SCHEMA_PLAYLIST.toSchemaRoot()) { list ->
            result.success(
                list.map { it.description.extras!!.getData<PlaylistWithCounts>()!!.toMap() }
            )
        }
    }

    fun getAlbums(call: MethodCall, result: MethodChannel.Result) {
        subscribe(MusicSchema.SCHEMA_ALBUM.toSchemaRoot()) { list ->
            result.success(
                list.map { it.description.extras!!.getData<AlbumWithCounts>()!!.toMap() }
            )
        }
    }

    fun getArtists(call: MethodCall, result: MethodChannel.Result) {
        subscribe(MusicSchema.SCHEMA_ARTIST.toSchemaRoot()) { list ->
            result.success(
                list.map { it.description.extras!!.getData<ArtistWithCounts>()!!.toMap() }
            )
        }
    }

    fun getAllMusic(call: MethodCall, result: MethodChannel.Result) {
        subscribe(MusicSchema.SCHEMA_ALL_MUSIC.toSchemaRoot()) { list ->
            result.success(
                list.map { it.description.extras!!.getData<MusicWithAlbumAndArtist>()!!.toMap() }
            )
        }
    }

    fun getFavoriteMusic(call: MethodCall, result: MethodChannel.Result) {
        subscribe(MusicSchema.SCHEMA_FAVORITE_MUSIC.toSchemaRoot()) { list ->
            result.success(
                list.map { it.description.extras!!.getData<MusicWithAlbumAndArtist>()!!.toMap() }
            )
        }
    }

    fun getNowPlaylist(call: MethodCall, result: MethodChannel.Result) {
        subscribe(MusicSchema.SCHEMA_NOW_PLAYLIST.toSchemaRoot()) { list ->
            result.success(
                list.map { it.description.extras!!.getData<MusicWithAlbumAndArtist>()!!.toMap() }
            )
        }
    }

    fun getMusics(call: MethodCall, result: MethodChannel.Result) {
        if (call.arguments != null) {
            subscribe(call.arguments as String) { list ->
                result.success(
                    list.map {
                        it.description.extras!!.getData<MusicWithAlbumAndArtist>()!!.toMap()
                    }
                )
            }
        } else {
            result.error(MusicPlayerPlugin.ERROR_EXCEPTION, "schemaId == null", null)
        }
    }

    private fun subscribe(
        schema: String,
        callback: (MutableList<MediaBrowserCompat.MediaItem>) -> Unit
    ) {
        mediaBrowser.unsubscribe(schema)
        mediaBrowser.subscribe(schema, object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                callback(children)
            }
        })
    }

    inner class ControllerCallback : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            currentMetadata = metadata
            refreshMusic()
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playState.value = state
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            when (event) {
                MediaSessionListener.Command.SCAN_CALLBACK -> {
                    // refreshAll()
                }
                MediaSessionListener.Command.REFRESH_NOW_PLAYLIST -> {
                    refreshNowPlaylist()
                }
            }
        }
    }

    inner class ConnectionCallback : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            connected.value = true
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
            mediaController?.registerCallback(ControllerCallback().apply {
                onMetadataChanged(mediaController?.metadata)
                onPlaybackStateChanged(mediaController?.playbackState)
            })
            refreshState()
        }

        override fun onConnectionFailed() {
            connected.value = false
        }

        override fun onConnectionSuspended() {
            connected.value = false
        }
    }

    data class MusicDuration(val position: Long, val duration: Long) {
        fun toMap(): Map<String, Any?> {
            return mapOf(
                "position" to position,
                "duration" to duration
            )
        }
    }
}