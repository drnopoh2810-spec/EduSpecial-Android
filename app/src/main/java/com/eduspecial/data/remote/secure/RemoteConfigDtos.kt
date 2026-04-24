package com.eduspecial.data.remote.secure

import com.google.gson.annotations.SerializedName

/** Encrypted envelope returned by /api/v1/config */
data class EncryptedConfigEnvelope(
    @SerializedName("v")   val version: Int,
    @SerializedName("ts")  val serverTsMs: Long,
    @SerializedName("iv")  val ivB64: String,
    @SerializedName("ct")  val ctB64: String,
    @SerializedName("tag") val tagB64: String
)

/** Decrypted config delivered by the secure channel. */
data class RuntimeConfig(
    @SerializedName("config_version")            val configVersion: Int = 1,
    @SerializedName("min_supported_app_version") val minSupportedAppVersion: Int = 1,
    @SerializedName("backend_base_url")          val backendBaseUrl: String = "",
    @SerializedName("firebase")                  val firebase: FirebaseConfigDto = FirebaseConfigDto(),
    @SerializedName("auth")                      val auth: AuthConfigDto = AuthConfigDto(),
    @SerializedName("cloudinary_accounts")       val cloudinaryAccounts: List<CloudinaryAccountDto> = emptyList(),
    @SerializedName("algolia")                   val algolia: AlgoliaConfigDto = AlgoliaConfigDto(),
    @SerializedName("feature_flags")             val featureFlags: Map<String, Boolean> = emptyMap(),
    @SerializedName("push_notifications")        val pushNotifications: PushNotificationsConfigDto = PushNotificationsConfigDto()
)

data class FirebaseConfigDto(
    @SerializedName("project_id")     val projectId: String = "",
    @SerializedName("project_number") val projectNumber: String = "",
    @SerializedName("application_id") val applicationId: String = "",
    @SerializedName("api_key")        val apiKey: String = "",
    @SerializedName("storage_bucket") val storageBucket: String = "",
    @SerializedName("database_url")   val databaseUrl: String = "",
    @SerializedName("web_client_id")  val webClientId: String = "",
    @SerializedName("android_client_id") val androidClientId: String = ""
)

data class AuthConfigDto(
    @SerializedName("provider")                   val provider: String = "firebase",
    @SerializedName("require_email_verification") val requireEmailVerification: Boolean = true,
    @SerializedName("allow_anonymous")            val allowAnonymous: Boolean = true,
    @SerializedName("session")                    val session: SessionConfigDto = SessionConfigDto(),
    @SerializedName("jwt")                        val jwt: JwtConfigDto = JwtConfigDto(),
    @SerializedName("google_signin")              val googleSignIn: GoogleSignInConfigDto = GoogleSignInConfigDto(),
    @SerializedName("password_policy")            val passwordPolicy: PasswordPolicyConfigDto = PasswordPolicyConfigDto(),
    @SerializedName("security")                   val security: SecurityConfigDto = SecurityConfigDto()
)

data class SessionConfigDto(
    @SerializedName("access_token_ttl_minutes") val accessTokenTtlMinutes: Int = 60,
    @SerializedName("refresh_token_ttl_days")   val refreshTokenTtlDays: Int = 30,
    @SerializedName("clock_skew_seconds")       val clockSkewSeconds: Int = 120
)

data class JwtConfigDto(
    @SerializedName("issuer")    val issuer: String = "",
    @SerializedName("audience")  val audience: String = "",
    @SerializedName("algorithm") val algorithm: String = "RS256",
    @SerializedName("jwks_url")  val jwksUrl: String = ""
)

data class GoogleSignInConfigDto(
    @SerializedName("enabled")           val enabled: Boolean = true,
    @SerializedName("web_client_id")     val webClientId: String = "",
    @SerializedName("android_client_id") val androidClientId: String = ""
)

data class PasswordPolicyConfigDto(
    @SerializedName("min_length")        val minLength: Int = 8,
    @SerializedName("require_uppercase") val requireUppercase: Boolean = true,
    @SerializedName("require_lowercase") val requireLowercase: Boolean = true,
    @SerializedName("require_digit")     val requireDigit: Boolean = true,
    @SerializedName("require_symbol")    val requireSymbol: Boolean = false
)

data class SecurityConfigDto(
    @SerializedName("max_login_attempts")   val maxLoginAttempts: Int = 5,
    @SerializedName("lockout_minutes")      val lockoutMinutes: Int = 15,
    @SerializedName("enable_device_binding") val enableDeviceBinding: Boolean = false
)

data class CloudinaryAccountDto(
    @SerializedName("cloud_name")    val cloudName: String,
    @SerializedName("upload_preset") val uploadPreset: String
)

data class AlgoliaConfigDto(
    @SerializedName("app_id")           val appId: String = "",
    @SerializedName("search_key")       val searchKey: String = "",
    @SerializedName("index_flashcards") val indexFlashcards: String = "flashcards",
    @SerializedName("index_questions")  val indexQuestions: String = "questions"
)

data class PushNotificationsConfigDto(
    @SerializedName("fcm_enabled") val fcmEnabled: Boolean = true,
    @SerializedName("topics")      val topics: List<String> = emptyList()
)
