package com.eduspecial.data.remote.config

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for all runtime configuration (Cloudinary, Algolia,
 * Backend URL, feature flags). Defaults are EMBEDDED in the app binary so the
 * app works fully offline / without any CI build-time secrets injection.
 *
 * Firebase Remote Config can override any of these values at runtime — but if
 * Remote Config never fetches successfully (no network, no project access), the
 * embedded defaults below keep the app functional.
 */
@Singleton
class RemoteConfigManager @Inject constructor() {

    companion object {
        private const val TAG = "RemoteConfigManager"
        private const val FETCH_TIMEOUT_SECONDS = 60L
        private const val MINIMUM_FETCH_INTERVAL_SECONDS = 3600L

        // ─── Embedded backend URL (admin/Flask backend in /myproject) ──────
        // Override via Remote Config key "backend_base_url" if hosting elsewhere.
        const val DEFAULT_BACKEND_BASE_URL = "https://eduspecial.replit.app/api/v1/"

        // ─── Embedded Cloudinary multi-account pool (cloud_name + preset) ──
        // Public values — safe to ship in the APK. API_SECRET stays on backend.
        private val EMBEDDED_DEFAULTS: Map<String, Any> = mapOf(
            // Cloudinary accounts (failover order 1 → 6)
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

            // Algolia (search-only key — safe to embed)
            "algolia_app_id" to "Z7D8XTTMQQ",
            "algolia_search_key" to "4a11418ac38720088b5bb00f2a9a5c50",
            "algolia_index_flashcards" to "flashcards",
            "algolia_index_questions" to "questions",

            // Backend (admin Flask in /myproject)
            "backend_base_url" to DEFAULT_BACKEND_BASE_URL,

            // Feature flags
            "feature_google_signin_enabled" to true,
            "min_supported_app_version" to 1L
        )
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
            remoteConfig.setDefaultsAsync(EMBEDDED_DEFAULTS)
            Log.d(TAG, "✅ Embedded defaults loaded (${EMBEDDED_DEFAULTS.size} keys)")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to setup Remote Config: ${e.message}")
        }
    }

    suspend fun fetchAndActivate(): Boolean {
        return try {
            val result = remoteConfig.fetchAndActivate().await()
            Log.d(TAG, if (result) "✅ Remote Config refreshed" else "ℹ️ Using cached/embedded config")
            result
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Remote Config fetch failed → using embedded defaults: ${e.message}")
            false
        }
    }

    fun getString(key: String): String = try {
        remoteConfig.getString(key)
    } catch (e: Exception) {
        Log.e(TAG, "❌ getString($key) failed: ${e.message}")
        ""
    }

    fun getBoolean(key: String): Boolean = try {
        remoteConfig.getBoolean(key)
    } catch (e: Exception) { false }

    fun getCloudinaryConfig(accountNumber: Int): CloudinaryConfig = CloudinaryConfig(
        cloudName = getString("cloudinary_cloud_name_$accountNumber"),
        uploadPreset = getString("cloudinary_upload_preset_$accountNumber")
    )

    fun getAlgoliaConfig(): AlgoliaConfig = AlgoliaConfig(
        appId = getString("algolia_app_id"),
        searchKey = getString("algolia_search_key")
    )

    fun getBackendBaseUrl(): String =
        getString("backend_base_url").ifEmpty { DEFAULT_BACKEND_BASE_URL }

    fun getConfigInfo(): String = try {
        val info = remoteConfig.info
        """
        Remote Config Status:
        - Last Fetch Status: ${info.lastFetchStatus}
        - Last Fetch Time: ${java.util.Date(info.fetchTimeMillis)}
        - Backend URL: ${getBackendBaseUrl()}
        """.trimIndent()
    } catch (e: Exception) {
        "Config info unavailable: ${e.message}"
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
