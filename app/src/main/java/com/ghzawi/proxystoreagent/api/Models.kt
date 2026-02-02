package com.ghzawi.proxystoreagent.api

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("pin") val pin: String
)

data class LoginResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("credits_balance") val creditsBalance: Int
)

data class PairingTokenRequest(
    @SerializedName("userId") val userId: Int
)

data class PairingTokenResponse(
    @SerializedName("pairing_token") val pairingToken: String
)

data class ErrorResponse(
    @SerializedName("error") val error: String
)
