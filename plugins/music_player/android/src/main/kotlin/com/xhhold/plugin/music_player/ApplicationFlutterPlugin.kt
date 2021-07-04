package com.xhhold.plugin.music_player

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.flutter.plugin.common.EventChannel
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
    ActivityAware, LifecycleOwner,
    MethodChannel.MethodCallHandler, EventChannel.StreamHandler,
    CoroutineScope {

    lateinit var channel: MethodChannel
    lateinit var eventChannel: EventChannel
    protected lateinit var context: Context
    private lateinit var binding: ActivityPluginBinding

    private val coroutineScopeContext = SupervisorJob() + Dispatchers.Main.immediate

    override val coroutineContext: CoroutineContext get() = coroutineScopeContext

    @CallSuper
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        channel = MethodChannel(binding.binaryMessenger, name)
        channel.setMethodCallHandler(this)
        eventChannel = EventChannel(binding.binaryMessenger, "$name/event")
        eventChannel.setStreamHandler(this)
    }

    @CallSuper
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        coroutineScopeContext.cancel()
        channel.setMethodCallHandler(null)
    }

    @CallSuper
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.binding = binding
    }

    @CallSuper
    override fun onDetachedFromActivity() {
    }

    @CallSuper
    override fun onDetachedFromActivityForConfigChanges() {
    }

    @CallSuper
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun getLifecycle(): Lifecycle {
        return (binding.lifecycle as HiddenLifecycleReference).lifecycle
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    }

    override fun onCancel(arguments: Any?) {
    }
}