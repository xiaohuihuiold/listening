package com.xhhold.plugin.music_player

import android.content.Context
import androidx.annotation.CallSuper
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * 带协程Scope以及Context的插件基类
 */
abstract class ApplicationFlutterPlugin(private val name: String) : FlutterPlugin,
        MethodChannel.MethodCallHandler, CoroutineScope {
    lateinit var channel: MethodChannel
    protected lateinit var context: Context
    private val coroutineScopeContext = SupervisorJob() + Dispatchers.Main.immediate

    override val coroutineContext: CoroutineContext get() = coroutineScopeContext

    @CallSuper
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        channel = MethodChannel(binding.binaryMessenger, name)
        channel.setMethodCallHandler(this)
    }

    @CallSuper
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        coroutineScopeContext.cancel()
        channel.setMethodCallHandler(null)
    }
}