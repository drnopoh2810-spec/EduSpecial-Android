package com.eduspecial.data.repository

import android.util.Log
import com.eduspecial.data.remote.config.AlgoliaConfig
import com.eduspecial.data.remote.config.CloudinaryConfig
import com.eduspecial.data.remote.config.RemoteConfigManager
import com.eduspecial.data.remote.secure.RuntimeConfigProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bootstraps the runtime configuration via [RuntimeConfigProvider] and exposes
 * Cloudinary / Algolia credentials to the rest of the app.
 *
 * No values are hard-coded; everything comes from the secure /api/v1/config
 * channel and is cached locally for offline starts.
 */
@Singleton
class ConfigRepository @Inject constructor(
    private val runtime: RuntimeConfigProvider,
    private val remoteConfigManager: RemoteConfigManager
) {
    companion object { private const val TAG = "ConfigRepository" }

    private val _isConfigLoaded = MutableStateFlow(false)
    val isConfigLoaded: StateFlow<Boolean> = _isConfigLoaded.asStateFlow()

    private val _configStatus = MutableStateFlow("Initializing...")
    val configStatus: StateFlow<String> = _configStatus.asStateFlow()

    suspend fun initializeConfig(): Boolean {
        return try {
            _configStatus.value = "Fetching encrypted runtime config..."
            val ok = runtime.bootstrap()

            _configStatus.value = if (ok) {
                "✅ Config loaded (${remoteConfigManager.getCloudinaryAccountCount()} cloudinary accounts)"
            } else {
                "❌ No config available — first run requires internet"
            }

            _isConfigLoaded.value = ok
            Log.d(TAG, remoteConfigManager.getConfigInfo())
            ok
        } catch (e: Exception) {
            Log.e(TAG, "❌ initializeConfig failed: ${e.message}")
            _configStatus.value = "❌ ${e.message}"
            _isConfigLoaded.value = false
            false
        }
    }

    fun getCloudinaryConfigs(): List<CloudinaryConfig> {
        val n = remoteConfigManager.getCloudinaryAccountCount()
        return (1..n).map { remoteConfigManager.getCloudinaryConfig(it) }
            .filter { it.cloudName.isNotEmpty() && it.uploadPreset.isNotEmpty() }
    }

    fun getAlgoliaConfig(): AlgoliaConfig = remoteConfigManager.getAlgoliaConfig()

    fun getConfigurationSummary(): String = """
        Configuration Status: ${_configStatus.value}
        Cloudinary Accounts:  ${getCloudinaryConfigs().size}
        Algolia Search:       ${if (getAlgoliaConfig().appId.isNotEmpty()) "Enabled" else "Disabled"}
        Config Loaded:        ${_isConfigLoaded.value}
    """.trimIndent()
}
