package com.xhhold.plugin.music_player

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/** 音乐播放器插件 */
class MusicPlayerPlugin : ApplicationFlutterPlugin("music_player") {

    companion object {
        private const val TAG = "MusicPlayerPlugin"
        private const val ERROR_EXCEPTION = "0x01"
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        runCatching {
            when (call.method) {
                else -> {
                    result.notImplemented()
                }
            }
        }.onFailure {
            result.error(ERROR_EXCEPTION, it.message, null)
        }
    }

}
