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
        private const val ERROR_EXCEPTION = "0x01"
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

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        runCatching {
            when (call.method) {
                "scan" -> {
                    musicHelper.scan()
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

}
