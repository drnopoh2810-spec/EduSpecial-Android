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
    @SerializedName("cloudinary_accounts")       val cloudinaryAccounts: List<CloudinaryAccountDto> = emptyList(),
    @SerializedName("algolia")                   val algolia: AlgoliaConfigDto = AlgoliaConfigDto(),
    @SerializedName("feature_flags")             val featureFlags: Map<String, Boolean> = emptyMap()
)

data class FirebaseConfigDto(
    @SerializedName("project_id")     val projectId: String = "",
    @SerializedName("project_number") val projectNumber: String = "",
    @SerializedName("application_id") val applicationId: String = "",
    @SerializedName("api_key")        val apiKey: String = "",
    @SerializedName("storage_bucket") val storageBucket: String = "",
    @SerializedName("database_url")   val databaseUrl: String = "",
    @SerializedName("web_client_id")  val webClientId: String = ""
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
