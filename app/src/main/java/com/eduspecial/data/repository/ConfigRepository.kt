package com.eduspecial.data.repository

import android.util.Log
import com.eduspecial.data.remote.config.AlgoliaConfig
import com.eduspecial.data.remote.config.CloudinaryConfig
import com.eduspecial.data.remote.config.RemoteConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager
) {
    
    companion object {
        private const val TAG = "ConfigRepository"
    }
    
    private val _isConfigLoaded = MutableStateFlow(false)
    val isConfigLoaded: StateFlow<Boolean> = _isConfigLoaded.asStateFlow()
    
    private val _configStatus = MutableStateFlow("Initializing...")
    val configStatus: StateFlow<String> = _configStatus.asStateFlow()
    
    suspend fun initializeConfig(): Boolean {
        return try {
            Log.d(TAG, "🚀 Initializing configuration...")
            _configStatus.value = "Fetching from Firebase..."
            
            val success = remoteConfigManager.fetchAndActivate()
            
            // Validate configuration
            val cloudinaryConfigs = getCloudinaryConfigs()
            val algoliaConfig = getAlgoliaConfig()
            
            Log.d(TAG, "📊 Configuration Summary:")
            Log.d(TAG, "  - Cloudinary accounts: ${cloudinaryConfigs.size}")
            Log.d(TAG, "  - Algolia configured: ${algoliaConfig.appId.isNotEmpty()}")
            
            _configStatus.value = if (success) {
                "✅ Configuration loaded from Firebase"
            } else {
                "⚠️ Using cached/default configuration"
            }
            
            _isConfigLoaded.value = true
            
            // Log Remote Config info
            Log.d(TAG, remoteConfigManager.getConfigInfo())
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Configuration initialization failed: ${e.message}")
            _configStatus.value = "❌ Configuration failed: ${e.message}"
            _isConfigLoaded.value = true // Still mark as loaded to use defaults
            false
        }
    }
    
    fun getCloudinaryConfigs(): List<CloudinaryConfig> {
        return try {
            (1..6).map { accountNumber ->
                remoteConfigManager.getCloudinaryConfig(accountNumber)
            }.filter { config ->
                config.cloudName.isNotEmpty() && 
                config.cloudName != "your_cloud_name" &&
                config.uploadPreset.isNotEmpty()
            }.also { configs ->
                Log.d(TAG, "📱 Available Cloudinary accounts: ${configs.size}")
                configs.forEachIndexed { index, config ->
                    Log.v(TAG, "  Account ${index + 1}: ${config.cloudName}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get Cloudinary configs: ${e.message}")
            emptyList()
        }
    }
    
    fun getAlgoliaConfig(): AlgoliaConfig {
        return try {
            remoteConfigManager.getAlgoliaConfig().also { config ->
                Log.d(TAG, "🔍 Algolia config: ${if (config.appId.isNotEmpty()) "Available" else "Not configured"}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get Algolia config: ${e.message}")
            AlgoliaConfig("", "")
        }
    }
    
    fun getConfigurationSummary(): String {
        val cloudinaryCount = getCloudinaryConfigs().size
        val algoliaAvailable = getAlgoliaConfig().appId.isNotEmpty()
        
        return """
        Configuration Status: ${_configStatus.value}
        Cloudinary Accounts: $cloudinaryCount
        Algolia Search: ${if (algoliaAvailable) "Enabled" else "Disabled"}
        Config Loaded: ${_isConfigLoaded.value}
        """.trimIndent()
    }
}