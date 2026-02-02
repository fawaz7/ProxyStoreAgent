package com.ghzawi.proxystoreagent.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProxyApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("host/pairing-token")
    suspend fun getPairingToken(@Body request: PairingTokenRequest): Response<PairingTokenResponse>
}
