package com.eduspecial.data.remote.config

import com.eduspecial.data.remote.secure.RuntimeConfigProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin façade over [RuntimeConfigProvider] kept for source-compatibility with
 * the rest of the codebase (CloudinaryService, AlgoliaSearchService, …).
 *
 * No keys, no defaults, no Firebase-RemoteConfig dependency — every value
 * comes from the encrypted runtime config delivered by /api/v1/config.
 */
@Singleton
class RemoteConfigManager @Inject constructor(
    private val runtimeConfigProvider: RuntimeConfigProvider
) {

    fun getCloudinaryConfig(accountNumber: Int): CloudinaryConfig {
        val account = runtimeConfigProvider.current
            ?.cloudinaryAccounts
            ?.getOrNull(accountNumber - 1)
        return CloudinaryConfig(
            cloudName    = account?.cloudName.orEmpty(),
            uploadPreset = account?.uploadPreset.orEmpty()
        )
    }

    fun getCloudinaryAccountCount(): Int =
        runtimeConfigProvider.current?.cloudinaryAccounts?.size ?: 0

    fun getAlgoliaConfig(): AlgoliaConfig {
        val algoliaConfig = runtimeConfigProvider.current?.algolia
        return AlgoliaConfig(
            appId     = algoliaConfig?.appId.orEmpty(),
            searchKey = algoliaConfig?.searchKey.orEmpty()
        )
    }

    fun getBackendBaseUrl(): String =
        runtimeConfigProvider.current?.backendBaseUrl.orEmpty()

    fun isFeatureEnabled(name: String): Boolean =
        runtimeConfigProvider.current?.featureFlags?.get(name) ?: false

    fun getConfigInfo(): String {
        val currentConfig = runtimeConfigProvider.current
        return """
            Runtime Config:
            - version: ${currentConfig?.configVersion ?: "n/a"}
            - cloudinary accounts: ${currentConfig?.cloudinaryAccounts?.size ?: 0}
            - algolia: ${if (!currentConfig?.algolia?.appId.isNullOrEmpty()) "configured" else "missing"}
            - backend: ${currentConfig?.backendBaseUrl ?: "n/a"}
        """.trimIndent()
    }
}

data class CloudinaryConfig(val cloudName: String, val uploadPreset: String)
data class AlgoliaConfig(val appId: String, val searchKey: String)
