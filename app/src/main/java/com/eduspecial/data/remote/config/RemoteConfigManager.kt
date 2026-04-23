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
    private val runtime: RuntimeConfigProvider
) {

    fun getCloudinaryConfig(accountNumber: Int): CloudinaryConfig {
        val account = runtime.current
            ?.cloudinaryAccounts
            ?.getOrNull(accountNumber - 1)
        return CloudinaryConfig(
            cloudName    = account?.cloudName.orEmpty(),
            uploadPreset = account?.uploadPreset.orEmpty()
        )
    }

    fun getCloudinaryAccountCount(): Int =
        runtime.current?.cloudinaryAccounts?.size ?: 0

    fun getAlgoliaConfig(): AlgoliaConfig {
        val a = runtime.current?.algolia
        return AlgoliaConfig(
            appId     = a?.appId.orEmpty(),
            searchKey = a?.searchKey.orEmpty()
        )
    }

    fun getBackendBaseUrl(): String =
        runtime.current?.backendBaseUrl.orEmpty()

    fun isFeatureEnabled(name: String): Boolean =
        runtime.current?.featureFlags?.get(name) ?: false

    fun getConfigInfo(): String {
        val c = runtime.current
        return """
            Runtime Config:
            - version: ${c?.configVersion ?: "n/a"}
            - cloudinary accounts: ${c?.cloudinaryAccounts?.size ?: 0}
            - algolia: ${if (!c?.algolia?.appId.isNullOrEmpty()) "configured" else "missing"}
            - backend: ${c?.backendBaseUrl ?: "n/a"}
        """.trimIndent()
    }
}

data class CloudinaryConfig(val cloudName: String, val uploadPreset: String)
data class AlgoliaConfig(val appId: String, val searchKey: String)
