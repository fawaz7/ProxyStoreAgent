package com.ghzawi.proxystoreagent.data

import android.content.Context
import android.provider.Settings
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

class PrefsManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "proxy_agent_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var pairingToken: String?
        get() = prefs.getString("pairing_token", null)
        set(value) = prefs.edit().putString("pairing_token", value).apply()

    var deviceId: Int
        get() = prefs.getInt("device_id", -1)
        set(value) = prefs.edit().putInt("device_id", value).apply()

    var deviceName: String?
        get() = prefs.getString("device_name", null)
        set(value) = prefs.edit().putString("device_name", value).apply()

    var deviceUsername: String?
        get() = prefs.getString("device_username", null)
        set(value) = prefs.edit().putString("device_username", value).apply()

    var isOnboarded: Boolean
        get() = prefs.getBoolean("is_onboarded", false)
        set(value) = prefs.edit().putBoolean("is_onboarded", value).apply()

    // Hardware ID - persistent device identifier
    fun getHardwareId(): String {
        var hwId = prefs.getString("hw_id", null)
        if (hwId == null) {
            // Try to use Android ID first
            hwId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            if (hwId.isNullOrBlank() || hwId == "9774d56d682e549c") {
                // Fallback to UUID if Android ID is invalid
                hwId = UUID.randomUUID().toString().replace("-", "")
            }
            prefs.edit().putString("hw_id", hwId).apply()
        }
        return hwId
    }

    fun clear() {
        val hwId = getHardwareId() // Preserve hardware ID
        prefs.edit().clear().apply()
        prefs.edit().putString("hw_id", hwId).apply()
    }
}
