package com.ghzawi.proxystoreagent.service

import com.google.gson.annotations.SerializedName

data class WsMessage(
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: String? = null,
    @SerializedName("payload") val payload: Any? = null
)

data class WelcomePayload(
    @SerializedName("device_id") val deviceId: Int,
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("username") val username: String
)

data class DataPayload(
    @SerializedName("data") val data: String
)

data class OffboardPayload(
    @SerializedName("reason") val reason: String
)

data class HttpRequestPayload(
    @SerializedName("method") val method: String,
    @SerializedName("url") val url: String,
    @SerializedName("headers") val headers: Map<String, List<String>>,
    @SerializedName("body") val body: String
)

data class HttpResponsePayload(
    @SerializedName("status") val status: Int,
    @SerializedName("headers") val headers: Map<String, List<String>>,
    @SerializedName("body") val body: String
)

object MessageType {
    const val WELCOME = "WELCOME"
    const val CONNECT = "CONNECT"
    const val CONNECTED = "CONNECTED"
    const val DATA = "DATA"
    const val CLOSE = "CLOSE"
    const val OFFBOARD = "OFFBOARD"
    const val REQUEST = "REQUEST"
    const val RESPONSE = "RESPONSE"
}
