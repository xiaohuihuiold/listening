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
import io.flutter.plugin.common.PluginRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

typealias OnPermissionsResult = (
    permissions: Array<out String>,
    grantResults: IntArray
) -> Unit

/**
 * 带协程Scope以及Context的插件基类
 */
abstract class ApplicationFlutterPlugin(private val name: String) : FlutterPlugin,
    ActivityAware, LifecycleOwner,
    PluginRegistry.RequestPermissionsResultListener,
    MethodChannel.MethodCallHandler, EventChannel.StreamHandler,
    CoroutineScope {

    lateinit var channel: MethodChannel
    lateinit var eventChannel: EventChannel
    protected lateinit var context: Context
    private lateinit var binding: ActivityPluginBinding

    /**
     * 请求回调等待
     */
    private val permissionsResults = ConcurrentHashMap<Int, OnPermissionsResult>()

    /**
     * 请求code生成
     */
    private val tempRequestCode = AtomicInteger()

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
        binding.addRequestPermissionsResultListener(this)
    }

    @CallSuper
    override fun onDetachedFromActivity() {
        binding.removeRequestPermissionsResultListener(this)
    }

    @CallSuper
    override fun onDetachedFromActivityForConfigChanges() {
    }

    @CallSuper
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>?,
        grantResults: IntArray?
    ): Boolean {
        val permissionsResult = permissionsResults[requestCode]
        if (permissionsResult != null) {
            permissionsResult(permissions ?: emptyArray(), grantResults ?: IntArray(0))
            permissionsResults.remove(requestCode)
            return true
        }
        return false
    }

    override fun getLifecycle(): Lifecycle {
        return (binding.lifecycle as HiddenLifecycleReference).lifecycle
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    }

    override fun onCancel(arguments: Any?) {
    }

    protected fun requestPermissions(permissions: Array<out String>, result: OnPermissionsResult) {
        val code = tempRequestCode.incrementAndGet()
        if (code >= 1000) {
            code.times(0)
        }
        permissionsResults[code] = result
        binding.activity.requestPermissions(permissions, code)
    }
}