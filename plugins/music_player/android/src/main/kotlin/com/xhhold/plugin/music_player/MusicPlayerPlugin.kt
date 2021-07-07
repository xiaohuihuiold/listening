package com.xhhold.plugin.music_player

import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/** 音乐播放器插件
 * io.flutter.embedding.engine.plugins.FlutterPlugin
 * */
class MusicPlayerPlugin : ApplicationFlutterPlugin("music_player") {

    private lateinit var musicHelper: MusicHelper

    companion object {
        private const val TAG = "MusicPlayerPlugin"
        const val ERROR_EXCEPTION = "0x01"
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        super.onAttachedToEngine(binding)
        musicHelper = MusicHelper(context)
        musicHelper.connect()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        musicHelper.disconnect()
        super.onDetachedFromEngine(binding)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        super.onAttachedToActivity(binding)
        initObserver()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        runCatching {
            when (call.method) {
                "scan" -> {
                    Log.i(TAG, "scan")
                    musicHelper.scan()
                    result.success(null)
                }
                "play" -> {
                    Log.i(TAG, "play")
                    val parentId = call.argument<String>("parentId")
                    val childId = call.argument<String>("childId")
                    if (parentId == null && childId == null) {
                        musicHelper.play()
                    } else if (parentId != null) {
                        musicHelper.playFromId(parentId, childId)
                    }
                    result.success(null)
                }
                "pause" -> {
                    Log.i(TAG, "pause")
                    musicHelper.pause()
                    result.success(null)
                }
                "skipToPrevious" -> {
                    Log.i(TAG, "skipToPrevious")
                    musicHelper.skipToPrevious()
                    result.success(null)
                }
                "skipToNext" -> {
                    Log.i(TAG, "skipToNext")
                    musicHelper.skipToPrevious()
                    result.success(null)
                }
                "seekTo" -> {
                    Log.i(TAG, "seekTo")
                    if (call.arguments != null) {
                        musicHelper.seekTo(call.arguments as Long)
                    }
                    result.success(null)
                }
                "getPlaylist" -> {
                    Log.i(TAG, "getPlaylist")
                    musicHelper.getPlaylist(call, result)
                }
                "getAlbums" -> {
                    Log.i(TAG, "getAlbums")
                    musicHelper.getAlbums(call, result)
                }
                "getArtists" -> {
                    Log.i(TAG, "getArtists")
                    musicHelper.getArtists(call, result)
                }
                "getAllMusic" -> {
                    Log.i(TAG, "getAllMusic")
                    musicHelper.getAllMusic(call, result)
                }
                "getFavoriteMusic" -> {
                    Log.i(TAG, "getFavoriteMusic")
                    musicHelper.getFavoriteMusic(call, result)
                }
                "getNowPlaylist" -> {
                    Log.i(TAG, "getNowPlaylist")
                    musicHelper.getNowPlaylist(call, result)
                }
                "getMusics" -> {
                    Log.i(TAG, "getMusics")
                    musicHelper.getMusics(call, result)
                }
                "refreshState" -> {
                    Log.i(TAG, "refreshState")
                    musicHelper.refreshState()
                    result.success(null)
                }
                else -> {
                    result.notImplemented()
                }
            }
        }.onFailure {
            result.error(ERROR_EXCEPTION, it.message, null)
        }
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        musicHelper.apply {

        }
    }

    private fun initObserver() {
        musicHelper.apply {
            connected.observe(this@MusicPlayerPlugin) {
                channel.invokeMethod("connected", it)
            }
            playState.observe(this@MusicPlayerPlugin) {
                channel.invokeMethod("playState", it?.state)
            }
            currentPosition.observe(this@MusicPlayerPlugin) {
                channel.invokeMethod("position", it?.toMap())

            }
            currentMusic.observe(this@MusicPlayerPlugin) {
                channel.invokeMethod("music", it?.toMap())
            }
            nowPlaylist.observe(this@MusicPlayerPlugin) {
                channel.invokeMethod("nowPlaylist", it.map { e -> e.toMap() })
            }
        }
    }

}
