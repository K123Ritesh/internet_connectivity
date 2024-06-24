package com.example.internet_connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternetConnectivityPlugin: FlutterPlugin, EventChannel.StreamHandler {
    private lateinit var channel : EventChannel
    private var networkMonitor: NetworkMonitor? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = EventChannel(flutterPluginBinding.binaryMessenger, "internet_connectivity")
        channel.setStreamHandler(this)
        networkMonitor = NetworkMonitor(flutterPluginBinding.applicationContext)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setStreamHandler(null)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        networkMonitor?.startMonitoring(events)
    }

    override fun onCancel(arguments: Any?) {
        networkMonitor?.stopMonitoring()
    }
}

class NetworkMonitor(private val context: Context) {

    private var eventSink: EventChannel.EventSink? = null
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var airplaneModeReceiver: BroadcastReceiver? = null

    init {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun startMonitoring(events: EventChannel.EventSink?) {
        eventSink = events
        registerNetworkCallback()
        registerAirplaneModeReceiver()
    }

    fun stopMonitoring() {
        unregisterNetworkCallback()
        unregisterAirplaneModeReceiver()
        eventSink = null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                notifyNetworkStatus("CONNECTED")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                notifyNetworkStatus("DISCONNECTED")
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
    }

    private fun unregisterNetworkCallback() {
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
    }

    private fun registerAirplaneModeReceiver() {
        airplaneModeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                    val isAirplaneModeOn = intent.getBooleanExtra("state", false)
                    if (isAirplaneModeOn) {
                        notifyNetworkStatus("AIRPLANE_MODE_ON")
                    } else {
                        notifyNetworkStatus("AIRPLANE_MODE_OFF")
                    }
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        context.registerReceiver(airplaneModeReceiver, filter)
    }

    private fun unregisterAirplaneModeReceiver() {
        airplaneModeReceiver?.let { context.unregisterReceiver(it) }
    }

    private fun notifyNetworkStatus(status: String) {
        CoroutineScope(Dispatchers.Main).launch {
            eventSink?.success(status)
        }
    }
}
