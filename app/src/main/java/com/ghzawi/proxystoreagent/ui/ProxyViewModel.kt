package com.ghzawi.proxystoreagent.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.ghzawi.proxystoreagent.data.PrefsManager
import com.ghzawi.proxystoreagent.service.ProxyService
import com.ghzawi.proxystoreagent.service.WelcomePayload

class ProxyViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "ProxyViewModel"
    private val prefs = PrefsManager(application)

    var connectionStatus by mutableStateOf("OFFLINE")
        private set

    var isConnecting by mutableStateOf(false)
        private set

    var deviceCredentials by mutableStateOf<WelcomePayload?>(null)
        private set

    var activeConnections by mutableStateOf(0)
        private set

    var totalBytesTransferred by mutableStateOf(0L)
        private set

    val pairingToken: String?
        get() = prefs.pairingToken

    private val statusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.ghzawi.proxystoreagent.STATUS_UPDATE" -> {
                    val status = intent.getStringExtra("status") ?: "OFFLINE"
                    connectionStatus = status

                    // Don't clear isConnecting if we're ONLINE or have credentials
                    if (status == "OFFLINE" || status == "UNAUTHORIZED") {
                        // Only clear isConnecting if we don't have credentials yet
                        if (deviceCredentials == null) {
                            isConnecting = false
                        }
                    }

                    // Handle 401 Unauthorized - device was removed from server
                    if (status == "UNAUTHORIZED") {
                        // Clear credentials from UI
                        deviceCredentials = null
                        isConnecting = false
                    }
                }
                "com.ghzawi.proxystoreagent.CREDENTIALS_UPDATE" -> {
                    val deviceId = intent.getIntExtra("deviceId", 0)
                    val deviceName = intent.getStringExtra("deviceName") ?: ""
                    val username = intent.getStringExtra("username") ?: ""

                    deviceCredentials = WelcomePayload(
                        deviceId = deviceId,
                        deviceName = deviceName,
                        username = username
                    )

                    // Clear loading state and set status to ONLINE
                    isConnecting = false
                    connectionStatus = "ONLINE"
                }
                "com.ghzawi.proxystoreagent.STATS_UPDATE" -> {
                    val activeConns = intent.getIntExtra("activeConnections", 0)
                    val totalBytes = intent.getLongExtra("totalBytes", 0L)
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction("com.ghzawi.proxystoreagent.STATUS_UPDATE")
            addAction("com.ghzawi.proxystoreagent.CREDENTIALS_UPDATE")
            addAction("com.ghzawi.proxystoreagent.STATS_UPDATE")
        }
        application.registerReceiver(statusReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        
        // Load stored device credentials if available
        if (prefs.deviceId != -1) {
            deviceCredentials = WelcomePayload(
                deviceId = prefs.deviceId,
                deviceName = prefs.deviceName ?: "",
                username = prefs.deviceUsername ?: ""
            )
        }

        // Auto-connect if device was previously onboarded
        if (prefs.isOnboarded) {
            autoConnect()
        }
    }

    private fun autoConnect() {
        isConnecting = true

        val intent = Intent(getApplication(), ProxyService::class.java)
        // For onboarded devices, token is optional (service will use hw_id)
        // Include token if available for backwards compatibility
        prefs.pairingToken?.let { intent.putExtra("TOKEN", it) }
        getApplication<Application>().startForegroundService(intent)
    }

    fun connect(token: String) {
        if (token.length != 6) {
            return
        }

        isConnecting = true

        // Store token
        prefs.pairingToken = token

        // Start service with token
        val intent = Intent(getApplication(), ProxyService::class.java)
        intent.putExtra("TOKEN", token)
        getApplication<Application>().startForegroundService(intent)
    }

    fun disconnect() {
        val intent = Intent(getApplication(), ProxyService::class.java)
        getApplication<Application>().stopService(intent)
        connectionStatus = "OFFLINE"
        isConnecting = false
    }

    fun offboard() {
        disconnect()
        
        // Clear all stored data
        prefs.clear()
        deviceCredentials = null
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(statusReceiver)
    }
}
