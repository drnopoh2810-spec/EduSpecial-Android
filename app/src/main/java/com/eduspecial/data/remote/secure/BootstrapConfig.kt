package com.eduspecial.data.remote.secure

/**
 * The ONLY secrets/configuration that ship inside the APK.
 *
 *  • [CONFIG_URL]          — where to fetch the encrypted runtime config from.
 *  • [SHARED_SECRET_HEX]   — 32-byte HMAC/AES-GCM key shared with the Flask
 *                            backend. Must match `shared_secret_hex` in
 *                            myproject/config.json.
 *
 * Every other key (Firebase, Cloudinary, Algolia, feature flags…) is fetched
 * at runtime over the secure channel and cached locally.
 *
 * To rotate the secret:
 *   1) Replace the value in myproject/config.json on the server.
 *   2) Replace [SHARED_SECRET_HEX] below.
 *   3) Rebuild & redeploy the APK.
 */
object BootstrapConfig {
    const val CONFIG_URL = "https://manoo22.pythonanywhere.com/api/v1/config"
    const val SHARED_SECRET_HEX =
        "3201d0946216d1d9ac81294932d0636dafc32eb13a211b80a00e7370abd83fa4"

    const val PROTOCOL_VERSION = 1
    const val HKDF_INFO = "eduspecial-config-v1"
    const val CACHE_TTL_MS = 60L * 60L * 1000L  // refresh hourly
}
