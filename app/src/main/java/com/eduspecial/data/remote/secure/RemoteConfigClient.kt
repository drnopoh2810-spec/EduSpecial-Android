package com.eduspecial.data.remote.secure

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fetches the encrypted runtime config from the Flask backend, verifies the
 * AES-GCM tag during decryption, and returns the parsed [RuntimeConfig].
 *
 * No keys are stored in code beyond the bootstrap shared secret in
 * [BootstrapConfig].
 */
@Singleton
class RemoteConfigClient @Inject constructor() {

    companion object { private const val TAG = "RemoteConfigClient" }

    private val gson = Gson()

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun fetch(): Result<RuntimeConfig> = withContext(Dispatchers.IO) {
        try {
            val ts    = System.currentTimeMillis()
            val nonce = SecureChannel.randomNonceHex()
            val sig   = SecureChannel.sign(
                method    = "GET",
                path      = "/api/v1/config",
                tsMs      = ts,
                nonce     = nonce,
                secretHex = BootstrapConfig.SHARED_SECRET_HEX
            )

            val request = Request.Builder()
                .url(BootstrapConfig.CONFIG_URL)
                .get()
                .addHeader("X-Timestamp",  ts.toString())
                .addHeader("X-Nonce",      nonce)
                .addHeader("X-Signature",  sig)
                .addHeader("X-App-Package","com.eduspecial.app")
                .addHeader("X-App-Version", "1")
                .addHeader("Accept", "application/json")
                .build()

            http.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return@withContext Result.failure(
                        RuntimeException("config fetch failed: HTTP ${resp.code}")
                    )
                }
                val body = resp.body?.string()
                    ?: return@withContext Result.failure(RuntimeException("empty response"))

                val env = gson.fromJson(body, EncryptedConfigEnvelope::class.java)
                if (env.version != BootstrapConfig.PROTOCOL_VERSION) {
                    return@withContext Result.failure(
                        RuntimeException("unsupported protocol version: ${env.version}")
                    )
                }

                val plaintext = SecureChannel.decryptConfigResponse(
                    ivB64      = env.ivB64,
                    ctB64      = env.ctB64,
                    tagB64     = env.tagB64,
                    serverTsMs = env.serverTsMs,
                    secretHex  = BootstrapConfig.SHARED_SECRET_HEX
                )

                val cfg = gson.fromJson(String(plaintext, Charsets.UTF_8), RuntimeConfig::class.java)
                Log.d(TAG, "✅ runtime config fetched & decrypted (v${cfg.configVersion}, " +
                        "${cfg.cloudinaryAccounts.size} cloudinary accounts)")
                Result.success(cfg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ secure config fetch failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
