package com.ghzawi.proxystoreagent.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Base64
import androidx.core.app.NotificationCompat
import com.ghzawi.proxystoreagent.MainActivity
import com.ghzawi.proxystoreagent.data.PrefsManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class ProxyService : Service() {

    private val tag = "ProxyService"
    private val notificationId = 1
    private val channelId = "proxy_agent_channel"

    private lateinit var prefs: PrefsManager
    private val okHttpClient = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    private var webSocket: WebSocket? = null
    private val gson = Gson()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val streams = ConcurrentHashMap<String, Socket>()
    private var wakeLock: PowerManager.WakeLock? = null
    
    private var isConnected = false
    private var isConnecting = false
    private var deviceCredentials: WelcomePayload? = null
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 3
    
    // Stats tracking
    private var totalBytesTransferred = 0L
    
    private fun broadcastStats() {
        val intent = Intent("com.ghzawi.proxystoreagent.STATS_UPDATE")
        intent.putExtra("activeConnections", streams.size)
        intent.putExtra("totalBytes", totalBytesTransferred)
        intent.setPackage(packageName)
        sendBroadcast(intent)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            if (!isConnected && !isConnecting) {
                if (currentToken != null || prefs.isOnboarded) {
                    serviceScope.launch {
                        delay(500)
                        if (!isConnected && !isConnecting) {
                            connectWebSocket()
                        }
                    }
                }
            }
        }
    }

    private var currentToken: String? = null

    override fun onCreate() {
        super.onCreate()
        prefs = PrefsManager(this)
        createNotificationChannel()
        startForeground(notificationId, createNotification("Starting..."))
        
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ProxyStore:WakeLock")
        wakeLock?.acquire(10 * 60 * 1000L /*10 minutes*/)
        
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val token = intent?.getStringExtra("TOKEN")

        if (token != null && token != currentToken) {
            currentToken = token
            connectWebSocket()
        } else if (token == null) {
            if (prefs.isOnboarded) {
                connectWebSocket()
            }
        } else if (token == currentToken) {
            if (!isConnected && !isConnecting) {
                connectWebSocket()
            }
        }
        return START_STICKY
    }

    private suspend fun fetchPublicIp(): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.ipify.org?format=text")
                .build()
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()?.trim()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun connectWebSocket() {
        if (isConnecting || isConnected) {
            return
        }

        val hwId = prefs.getHardwareId()
        val isOnboarded = prefs.isOnboarded

        isConnecting = true

        serviceScope.launch {
            val publicIp = fetchPublicIp() ?: "unknown"

            val url = if (isOnboarded) {
                "ws://89.167.8.47:8080/ws/agent?hw_id=$hwId&ip=$publicIp&platform=android"
            } else {
                val token = currentToken ?: run {
                    isConnecting = false
                    broadcastStatus("ERROR: No token")
                    return@launch
                }
                "ws://89.167.8.47:8080/ws/agent?token=$token&hw_id=$hwId&ip=$publicIp&platform=android"
            }
        
            val request = Request.Builder()
                .url(url)
                .header("Origin", "http://89.167.8.47:8080")
                .build()

            webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                isConnecting = false
                reconnectAttempts = 0
                updateNotification("ONLINE")
                broadcastStatus("ONLINE")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
                handleBinaryMessage(bytes.toByteArray())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                isConnecting = false
                updateNotification("OFFLINE")
                broadcastStatus("OFFLINE")
                if (code != 1000) {
                    reconnect()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                isConnecting = false
                updateNotification("OFFLINE")

                if (response?.code == 401) {
                    prefs.isOnboarded = false
                    prefs.deviceId = -1
                    prefs.deviceName = null
                    prefs.deviceUsername = null
                    broadcastStatus("UNAUTHORIZED")
                    return
                }

                broadcastStatus("OFFLINE")
                reconnect()
            }
            })
        }
    }

    private fun reconnect() {
        serviceScope.launch {
            if (reconnectAttempts >= maxReconnectAttempts) {
                return@launch
            }
            reconnectAttempts++
            delay(5000)
            if (!isConnected && !isConnecting) {
                connectWebSocket()
            }
        }
    }

    private fun handleMessage(text: String) {
        try {
            val json = gson.fromJson(text, JsonObject::class.java)

            val type = json.get("type")?.asString
            val id = json.get("id")?.asString

            if (type == null) {
                return
            }

            when (type) {
                MessageType.WELCOME -> {
                    val payloadElement = json.get("payload")

                    val payload = if (payloadElement.isJsonObject) {
                        gson.fromJson(payloadElement, WelcomePayload::class.java)
                    } else {
                        gson.fromJson(payloadElement.asString, WelcomePayload::class.java)
                    }

                    deviceCredentials = payload
                    prefs.deviceId = payload.deviceId
                    prefs.deviceName = payload.deviceName
                    prefs.deviceUsername = payload.username
                    prefs.isOnboarded = true
                    broadcastCredentials(payload)
                }
                MessageType.REQUEST -> {
                    val payloadElement = json.get("payload")
                    val payload = if (payloadElement.isJsonObject) {
                        gson.fromJson(payloadElement, HttpRequestPayload::class.java)
                    } else {
                        gson.fromJson(payloadElement.asString, HttpRequestPayload::class.java)
                    }
                    if (id != null) handleHttpRequest(id, payload)
                }
                MessageType.CONNECT -> {
                    val target = json.get("payload").asString
                    if (id != null) handleConnect(id, target)
                }
                MessageType.DATA -> {
                    val payloadElement = json.get("payload")
                    val payloadJson = if (payloadElement.isJsonObject) {
                        payloadElement.asJsonObject
                    } else {
                        gson.fromJson(payloadElement.asString, JsonObject::class.java)
                    }
                    val base64Data = payloadJson.get("data").asString
                    if (id != null) handleData(id, base64Data)
                }
                MessageType.CLOSE -> {
                    if (id != null) handleClose(id, notifyServer = false)
                }
                MessageType.OFFBOARD -> {
                    stopSelf()
                }
            }
        } catch (e: Exception) {
            // Silently ignore message parsing errors
        }
    }

    private fun handleHttpRequest(requestId: String, payload: HttpRequestPayload) {
        serviceScope.launch {
            try {
                // Build OkHttp request
                val requestBuilder = Request.Builder().url(payload.url)
                
                // Add headers (skip Proxy-Authorization)
                payload.headers.forEach { (name, values) ->
                    if (name.equals("Proxy-Authorization", ignoreCase = true)) return@forEach
                    values.forEach { value ->
                        requestBuilder.addHeader(name, value)
                    }
                }
                
                // Add body if present
                if (payload.body.isNotEmpty()) {
                    val contentType = payload.headers["Content-Type"]?.firstOrNull() ?: "text/plain"
                    val requestBody = payload.body.toRequestBody(contentType.toMediaType())
                    requestBuilder.method(payload.method, requestBody)
                } else {
                    requestBuilder.method(payload.method, null)
                }
                
                val response = httpClient.newCall(requestBuilder.build()).execute()
                
                // Build response headers map
                val responseHeaders = mutableMapOf<String, List<String>>()
                response.headers.names().forEach { name ->
                    responseHeaders[name] = response.headers.values(name)
                }
                
                // Get response body
                val responseBody = response.body?.string() ?: ""
                
                // Update stats
                totalBytesTransferred += responseBody.length
                broadcastStats()
                
                // Send RESPONSE back to server - payload as object (matches incoming REQUEST format)
                val httpResponse = HttpResponsePayload(
                    status = response.code,
                    headers = responseHeaders,
                    body = responseBody
                )
                val responseMap = mapOf(
                    "type" to MessageType.RESPONSE,
                    "id" to requestId,
                    "payload" to httpResponse  // Object, not string!
                )
                val responseJson = gson.toJson(responseMap)
                webSocket?.send(responseJson)
                
                response.close()
            } catch (e: Exception) {
                // Send error response - payload as object
                val errorResponse = HttpResponsePayload(
                    status = 500,
                    headers = mapOf("Content-Type" to listOf("text/plain")),
                    body = "Proxy error: ${e.message}"
                )
                val errorMap = mapOf(
                    "type" to MessageType.RESPONSE,
                    "id" to requestId,
                    "payload" to errorResponse  // Object, not string!
                )
                webSocket?.send(gson.toJson(errorMap))
            }
        }
    }

    private fun handleConnect(streamId: String, target: String) {
        serviceScope.launch {
            try {
                val parts = target.split(":")
                if (parts.size != 2) {
                    handleClose(streamId, notifyServer = false)
                    return@launch
                }
                val host = parts[0]
                val port = parts[1].toIntOrNull()
                if (port == null) {
                    handleClose(streamId, notifyServer = false)
                    return@launch
                }
                
                val inetAddress = withContext(Dispatchers.IO) {
                    java.net.InetAddress.getByName(host)
                }

                val socket = Socket()
                socket.connect(InetSocketAddress(inetAddress, port), 10000)
                socket.soTimeout = 30000  // 30 second idle timeout (reasonable for HTTP keep-alive)
                streams[streamId] = socket
                
                // Update stats for new connection
                broadcastStats()
                
                // Send CONNECTED response (no payload)
                val response = WsMessage(type = MessageType.CONNECTED, id = streamId)
                val responseJson = gson.toJson(response)
                webSocket?.send(responseJson)
                
                // Start reading from target
                startReadingFromTarget(streamId, socket)
            } catch (e: Exception) {
                handleClose(streamId, notifyServer = false)
            }
        }
    }

    private fun startReadingFromTarget(streamId: String, socket: Socket) {
        serviceScope.launch {
            // Use 256KB buffer for high-performance data transfer
            val buffer = ByteArray(BinaryFrameCodec.MAX_BUFFER_SIZE)
            try {
                val inputStream: InputStream = socket.getInputStream()
                while (isActive && !socket.isClosed) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) {
                        break
                    }

                    // HIGH-PERFORMANCE PATH: Send binary frame (no Base64, no JSON)
                    val dataToSend = buffer.copyOfRange(0, bytesRead)
                    val binaryFrame = BinaryFrameCodec.encodeBinaryFrame(
                        BinaryFrameCodec.BINARY_FRAME_DATA,
                        streamId,
                        dataToSend
                    )

                    webSocket?.send(okio.ByteString.of(*binaryFrame))

                    // Update stats
                    totalBytesTransferred += bytesRead
                    broadcastStats()
                }
            } catch (e: Exception) {
                // Handle errors silently
            } finally {
                handleCloseBinary(streamId)
            }
        }
    }

    /**
     * Handle binary WebSocket message (high-performance data plane)
     * Uses binary frame protocol - no JSON, no Base64 encoding
     */
    private fun handleBinaryMessage(frame: ByteArray) {
        val decoded = BinaryFrameCodec.decodeBinaryFrame(frame)

        if (decoded == null) {
            return
        }

        val (frameType, streamId, data) = decoded

        when (frameType) {
            BinaryFrameCodec.BINARY_FRAME_DATA -> {
                handleBinaryData(streamId, data)
            }
            BinaryFrameCodec.BINARY_FRAME_CLOSE -> {
                handleClose(streamId, notifyServer = false)
            }
        }
    }

    /**
     * Handle binary data frame (optimized path - no Base64 decoding)
     */
    private fun handleBinaryData(streamId: String, data: ByteArray) {
        val socket = streams[streamId]
        if (socket == null) {
            return
        }

        if (socket.isClosed) {
            return
        }

        serviceScope.launch {
            try {
                socket.getOutputStream().write(data)
                socket.getOutputStream().flush()

                // Update stats
                totalBytesTransferred += data.size
                broadcastStats()
            } catch (e: Exception) {
                handleClose(streamId, notifyServer = true)
            }
        }
    }

    /**
     * Handle JSON data message (legacy/fallback path - with Base64 decoding)
     */
    private fun handleData(streamId: String, base64Data: String) {
        val socket = streams[streamId]
        if (socket == null) {
            return
        }
        if (socket.isClosed) {
            return
        }

        serviceScope.launch {
            try {
                val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                socket.getOutputStream().write(bytes)
                socket.getOutputStream().flush()

                // Update stats
                totalBytesTransferred += bytes.size
                broadcastStats()
            } catch (e: Exception) {
                handleClose(streamId, notifyServer = true)
            }
        }
    }

    private fun handleClose(streamId: String, notifyServer: Boolean) {
        val socket = streams.remove(streamId)
        try {
            socket?.close()
        } catch (e: Exception) {
            // Ignore errors
        }
        if (notifyServer) {
            val closeMessage = WsMessage(type = MessageType.CLOSE, id = streamId)
            val closeJson = gson.toJson(closeMessage)
            webSocket?.send(closeJson)
        }
        // Update stats after connection closes
        broadcastStats()
    }

    /**
     * Close stream and send binary CLOSE frame (high-performance path)
     */
    private fun handleCloseBinary(streamId: String) {
        val socket = streams.remove(streamId)
        try {
            socket?.close()
        } catch (e: Exception) {
            // Ignore errors
        }

        // Send binary CLOSE frame
        val closeFrame = BinaryFrameCodec.encodeBinaryFrame(
            BinaryFrameCodec.BINARY_FRAME_CLOSE,
            streamId
        )
        webSocket?.send(okio.ByteString.of(*closeFrame))

        // Update stats after connection closes
        broadcastStats()
    }

    private fun broadcastStatus(status: String) {
        val intent = Intent("com.ghzawi.proxystoreagent.STATUS_UPDATE")
        intent.putExtra("status", status)
        intent.setPackage(packageName)
        sendBroadcast(intent)
    }

    private fun broadcastCredentials(payload: WelcomePayload) {
        val intent = Intent("com.ghzawi.proxystoreagent.CREDENTIALS_UPDATE")
        intent.putExtra("deviceId", payload.deviceId)
        intent.putExtra("deviceName", payload.deviceName)
        intent.putExtra("username", payload.username)
        intent.setPackage(packageName)
        sendBroadcast(intent)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            channelId,
            "ProxyStore Agent Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    private fun createNotification(status: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("ProxyStore Agent")
            .setContentText("Status: $status")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(status: String) {
        val notification = createNotification(status)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
        
        serviceScope.cancel()
        webSocket?.close(1000, "Service destroyed")
        streams.values.forEach { 
            try { it.close() } catch (_: Exception) {}
        }
        streams.clear()
        wakeLock?.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
