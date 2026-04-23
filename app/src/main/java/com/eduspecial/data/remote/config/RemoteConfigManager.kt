package com.eduspecial.data.remote.config

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManager @Inject constructor() {
    
    companion object {
        private const val TAG = "RemoteConfigManager"
        private const val FETCH_TIMEOUT_SECONDS = 60L
        private const val MINIMUM_FETCH_INTERVAL_SECONDS = 3600L // 1 hour
    }
    
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    
    init {
        setupRemoteConfig()
    }
    
    private fun setupRemoteConfig() {
        try {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = MINIMUM_FETCH_INTERVAL_SECONDS
                fetchTimeoutInSeconds = FETCH_TIMEOUT_SECONDS
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            
            // Set default values for offline scenarios
            val defaults = mapOf(
                "cloudinary_cloud_name_1" to "ddh0htmsd",
                "cloudinary_upload_preset_1" to "eduspecial_preset",
                "cloudinary_cloud_name_2" to "dia9sbo8k",
                "cloudinary_upload_preset_2" to "eduspecial_preset",
                "cloudinary_cloud_name_3" to "df3laarle",
                "cloudinary_upload_preset_3" to "eduspecial_preset",
                "cloudinary_cloud_name_4" to "dxhwzdawf",
                "cloudinary_upload_preset_4" to "eduspecial_preset",
                "cloudinary_cloud_name_5" to "ddybezumc",
                "cloudinary_upload_preset_5" to "eduspecial_preset",
                "cloudinary_cloud_name_6" to "dmxcipnbf",
                "cloudinary_upload_preset_6" to "eduspecial_preset",
                "algolia_app_id" to "Z7D8XTTMQQ",
                "algolia_search_key" to "4a11418ac38720088b5bb00f2a9a5c50"
            )
            remoteConfig.setDefaultsAsync(defaults)
            Log.d(TAG, "✅ Remote Config defaults set successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to setup Remote Config: ${e.message}")
        }
    }
    
    suspend fun fetchAndActivate(): Boolean {
        return try {
            Log.d(TAG, "🔄 Fetching Remote Config...")
            val result = remoteConfig.fetchAndActivate().await()
            if (result) {
                Log.d(TAG, "✅ Remote Config fetched and activated successfully")
            } else {
                Log.w(TAG, "⚠️ Remote Config fetch completed but no new config")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to fetch Remote Config: ${e.message}")
            Log.i(TAG, "📱 Using cached/default values")
            false
        }
    }
    
    fun getString(key: String): String {
        return try {
            val value = remoteConfig.getString(key)
            Log.v(TAG, "📖 Retrieved config: $key = ${if (value.length > 20) "${value.take(20)}..." else value}")
            value
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get config for key: $key, error: ${e.message}")
            ""
        }
    }
    
    fun getCloudinaryConfig(accountNumber: Int): CloudinaryConfig {
        return CloudinaryConfig(
            cloudName = getString("cloudinary_cloud_name_$accountNumber"),
            uploadPreset = getString("cloudinary_upload_preset_$accountNumber")
        )
    }
    
    fun getAlgoliaConfig(): AlgoliaConfig {
        return AlgoliaConfig(
            appId = getString("algolia_app_id"),
            searchKey = getString("algolia_search_key")
        )
    }
    
    fun getConfigInfo(): String {
        return try {
            val info = remoteConfig.info
            """
            Remote Config Status:
            - Last Fetch Status: ${info.lastFetchStatus}
            - Last Fetch Time: ${java.util.Date(info.fetchTimeMillis)}
            - Config Settings: ${info.configSettings}
            """.trimIndent()
        } catch (e: Exception) {
            "Config info unavailable: ${e.message}"
        }
    }
}

data class CloudinaryConfig(
    val cloudName: String,
    val uploadPreset: String
)

data class AlgoliaConfig(
    val appId: String,
    val searchKey: String
)