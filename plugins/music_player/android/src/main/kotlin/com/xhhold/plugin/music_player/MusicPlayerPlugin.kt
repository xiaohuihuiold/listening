package com.xhhold.plugin.music_player

import io.flutter.embedding.engine.plugins.FlutterPlugin
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
        initObserve()
        musicHelper.connect()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        musicHelper.disconnect()
        super.onDetachedFromEngine(binding)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        runCatching {
            when (call.method) {
                "scan" -> {
                    musicHelper.scan()
                    result.success(null)
                }
                "play" -> {
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
                    musicHelper.pause()
                    result.success(null)
                }
                "skipToPrevious" -> {
                    musicHelper.skipToPrevious()
                    result.success(null)
                }
                "skipToNext" -> {
                    musicHelper.skipToPrevious()
                    result.success(null)
                }
                "seekTo" -> {
                    if (call.arguments != null) {
                        musicHelper.seekTo(call.arguments as Long)
                    }
                    result.success(null)
                }
                "getMusic" -> {
                    musicHelper.getMusic(call, result)
                }
                "getPlaylist" -> {
                    musicHelper.getPlaylist(call, result)
                }
                "getAlbums" -> {
                    musicHelper.getAlbums(call, result)
                }
                "getArtists" -> {
                    musicHelper.getArtists(call, result)
                }
                "getAllMusic" -> {
                    musicHelper.getAllMusic(call, result)
                }
                "getFavoriteMusic" -> {
                    musicHelper.getFavoriteMusic(call, result)
                }
                "getNowPlaylist" -> {
                    musicHelper.getNowPlaylist(call, result)
                }
                "getMusics" -> {
                    musicHelper.getMusics(call, result)
                }
                else -> {
                    result.notImplemented()
                }
            }
        }.onFailure {
            result.error(ERROR_EXCEPTION, it.message, null)
        }
    }

    private fun initObserve() {
        musicHelper.apply {
            connected.observe(this@MusicPlayerPlugin) {
                channel.invokeMethod("connected", it)
            }
            playState.observe(this@MusicPlayerPlugin) {
                channel.invokeMethod("playState", it?.state)
            }
            currentPosition.observe(this@MusicPlayerPlugin) {
                // channel.invokeMethod("currentPosition", it?.toMap())
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
