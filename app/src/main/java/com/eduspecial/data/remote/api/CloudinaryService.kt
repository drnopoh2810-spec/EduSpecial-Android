package com.eduspecial.data.remote.api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.eduspecial.data.repository.ConfigRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Multi-account Cloudinary service with automatic failover.
 *
 * Strategy:
 *  1. Try the PRIMARY account first
 *  2. If it fails (quota exceeded, network error, etc.) → try ACCOUNT_2
 *  3. If that fails → try ACCOUNT_3
 *  4. If all fail → return Failure
 *
 * To add more accounts: add entries to [cloudinaryAccounts] in local.properties
 * and register them in [CloudinaryAccountRegistry].
 */
@Singleton
class CloudinaryService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configRepository: ConfigRepository
) {
    companion object {
        private const val TAG = "CloudinaryService"
        private const val EDUSPECIAL_FOLDER = "eduspecial"
    }

    private var initializedCloudName: String? = null

    fun initialize() {
        // Initialize with the first available account from Remote Config
        val accounts = getAccountsFromConfig()
        val primary = accounts.firstOrNull() ?: return
        initializeWithAccount(primary)
    }

    private fun getAccountsFromConfig(): List<CloudinaryAccount> {
        return configRepository.getCloudinaryConfigs().mapIndexed { index, config ->
            CloudinaryAccount(
                cloudName = config.cloudName,
                uploadPreset = config.uploadPreset,
                priority = index + 1
            )
        }
    }

    private fun initializeWithAccount(account: CloudinaryAccount) {
        if (initializedCloudName == account.cloudName) return
        try {
            val config = mapOf("cloud_name" to account.cloudName, "secure" to true)
            // MediaManager can only be initialized once — reinitialize by recreating config
            try {
                MediaManager.init(context, config)
            } catch (e: IllegalStateException) {
                // Already initialized — update config via reflection or accept current state
                Log.w(TAG, "MediaManager already initialized, using existing instance")
            }
            initializedCloudName = account.cloudName
            Log.d(TAG, "Initialized with account: ${account.cloudName}")
        } catch (e: Exception) {
            Log.w(TAG, "Init error: ${e.message}")
            initializedCloudName = account.cloudName
        }
    }

    /**
     * Upload with automatic failover across all registered accounts.
     * Tries each account in order until one succeeds.
     * [onProgress] is called with 0–100 as the upload progresses.
     */
    suspend fun uploadMedia(
        uri: Uri,
        resourceType: String = "auto",
        folder: String = EDUSPECIAL_FOLDER,
        onProgress: ((Int) -> Unit)? = null
    ): UploadResult {
        val accounts = getAccountsFromConfig()
        if (accounts.isEmpty()) return UploadResult.Failure("No Cloudinary accounts configured")

        var lastError = "Upload failed"

        for (account in accounts) {
            Log.d(TAG, "Trying upload with account: ${account.cloudName}")
            initializeWithAccount(account)

            val result = tryUpload(uri, resourceType, folder, account, onProgress)

            when (result) {
                is UploadResult.Success -> {
                    Log.d(TAG, "Upload succeeded with account: ${account.cloudName}")
                    account.markHealthy()
                    return result
                }
                is UploadResult.Failure -> {
                    Log.w(TAG, "Upload failed with ${account.cloudName}: ${result.error}")
                    account.markFailed()
                    lastError = result.error

                    // Check if it's a quota error — skip this account permanently
                    if (result.error.contains("quota", ignoreCase = true) ||
                        result.error.contains("storage", ignoreCase = true) ||
                        result.error.contains("limit", ignoreCase = true)) {
                        Log.w(TAG, "Quota exceeded for ${account.cloudName}, marking as full")
                        account.markQuotaExceeded()
                    }
                }
            }
        }

        return UploadResult.Failure("All accounts failed. Last error: $lastError")
    }

    private suspend fun tryUpload(
        uri: Uri,
        resourceType: String,
        folder: String,
        account: CloudinaryAccount,
        onProgress: ((Int) -> Unit)? = null
    ): UploadResult = suspendCancellableCoroutine { continuation ->

        val requestId = MediaManager.get()
            .upload(uri)
            .unsigned(account.uploadPreset)  // ✅ Add upload preset here!
            .option("folder", folder)
            .option("resource_type", resourceType)
            .option("quality", "auto")
            .option("fetch_format", "auto")
            .option("flags", "progressive")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    if (totalBytes > 0) {
                        val pct = ((bytes * 100) / totalBytes).toInt().coerceIn(0, 99)
                        onProgress?.invoke(pct)
                    }
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url") as? String ?: ""
                    val publicId = resultData?.get("public_id") as? String ?: ""
                    val resType = resultData?.get("resource_type") as? String ?: "image"
                    if (!continuation.isCompleted)
                        continuation.resume(UploadResult.Success(url, publicId, resType))
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    if (!continuation.isCompleted)
                        continuation.resume(UploadResult.Failure(error?.description ?: "Upload failed"))
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    if (!continuation.isCompleted)
                        continuation.resume(UploadResult.Failure("Rescheduled: ${error?.description}"))
                }
            })
            .dispatch()

        continuation.invokeOnCancellation {
            try { MediaManager.get().cancelRequest(requestId) } catch (_: Exception) {}
        }
    }

    // ─── URL Generators ───────────────────────────────────────────────────────

    fun getOptimizedImageUrl(publicId: String, width: Int = 800, height: Int? = null): String {
        val accounts = getAccountsFromConfig()
        val cloudName = initializedCloudName ?: accounts.firstOrNull()?.cloudName ?: ""
        val heightPart = if (height != null) ",h_$height" else ""
        return "https://res.cloudinary.com/$cloudName/image/upload/f_auto,q_auto,w_$width$heightPart/$publicId"
    }

    fun getHlsStreamUrl(publicId: String): String {
        val cloudName = initializedCloudName ?: ""
        return "https://res.cloudinary.com/$cloudName/video/upload/sp_hd/$publicId.m3u8"
    }

    fun getVideoThumbnailUrl(publicId: String, timeOffset: Float = 0f): String {
        val cloudName = initializedCloudName ?: ""
        return "https://res.cloudinary.com/$cloudName/video/upload/f_auto,q_auto,so_${timeOffset}/$publicId.jpg"
    }

    fun getAudioUrl(publicId: String): String {
        val cloudName = initializedCloudName ?: ""
        return "https://res.cloudinary.com/$cloudName/video/upload/$publicId.mp3"
    }
}

// ─── Account Model ────────────────────────────────────────────────────────────

data class CloudinaryAccount(
    val cloudName: String,
    val uploadPreset: String,
    val priority: Int = 0  // lower = higher priority
) {
    @Volatile var isQuotaExceeded: Boolean = false
        private set
    @Volatile var consecutiveFailures: Int = 0
        private set

    fun markHealthy() { consecutiveFailures = 0 }
    fun markFailed() { consecutiveFailures++ }
    fun markQuotaExceeded() { isQuotaExceeded = true }

    /** Account is usable if quota not exceeded and not failing too much */
    val isAvailable: Boolean
        get() = !isQuotaExceeded && consecutiveFailures < 3
}

// ─── Account Registry ─────────────────────────────────────────────────────────

/**
 * Holds all configured Cloudinary accounts sorted by priority.
 * Now gets accounts from Firebase Remote Config instead of BuildConfig.
 */
@Singleton
class CloudinaryAccountRegistry @Inject constructor(
    private val configRepository: ConfigRepository
) {

    fun getAllAccounts(): List<CloudinaryAccount> {
        return configRepository.getCloudinaryConfigs().mapIndexed { index, config ->
            CloudinaryAccount(
                cloudName = config.cloudName,
                uploadPreset = config.uploadPreset,
                priority = index + 1
            )
        }
    }

    /** Returns the best available account (not quota-exceeded, fewest failures) */
    fun getNextAvailable(): CloudinaryAccount? =
        getAllAccounts().filter { it.isAvailable }.minByOrNull { it.consecutiveFailures }

    fun getStatus(): String = getAllAccounts().joinToString("\n") { acc ->
        "• ${acc.cloudName}: ${if (acc.isQuotaExceeded) "QUOTA FULL" else if (acc.isAvailable) "OK" else "FAILING (${acc.consecutiveFailures} errors)"}"
    }
}

sealed class UploadResult {
    data class Success(val url: String, val publicId: String, val resourceType: String) : UploadResult()
    data class Failure(val error: String) : UploadResult()
}
