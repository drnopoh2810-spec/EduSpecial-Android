package com.eduspecial.data.remote.api

import com.eduspecial.data.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface EduSpecialApiService {

    // ─── Auth ─────────────────────────────────────────────────────────────────
    /**
     * Called after Firebase registration to create the Firestore user document.
     * The Firebase ID token is attached automatically by OkHttp interceptor.
     */
    @POST("auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("auth/me")
    suspend fun getMyProfile(): Response<UserProfileDto>

    // ─── Flashcards ───────────────────────────────────────────────────────────
    @GET("flashcards")
    suspend fun getFlashcards(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") category: String? = null
    ): Response<PaginatedResponse<FlashcardDto>>

    @POST("flashcards")
    suspend fun createFlashcard(
        @Body request: CreateFlashcardRequest
    ): Response<FlashcardDto>

    @POST("flashcards/check-duplicate")
    suspend fun checkDuplicate(
        @Body request: DuplicateCheckRequest
    ): Response<DuplicateCheckResponse>

    @GET("flashcards/{id}")
    suspend fun getFlashcard(@Path("id") id: String): Response<FlashcardDto>

    @DELETE("flashcards/{id}")
    suspend fun deleteFlashcard(@Path("id") id: String): Response<Unit>

    @PATCH("flashcards/{id}")
    suspend fun updateFlashcard(
        @Path("id") id: String,
        @Body request: UpdateFlashcardRequest
    ): Response<FlashcardDto>

    // ─── Q&A ─────────────────────────────────────────────────────────────────
    @GET("questions")
    suspend fun getQuestions(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") category: String? = null,
        @Query("unanswered_only") unansweredOnly: Boolean = false
    ): Response<PaginatedResponse<QAQuestionDto>>

    @POST("questions")
    suspend fun createQuestion(
        @Body request: CreateQuestionRequest
    ): Response<QAQuestionDto>

    @POST("questions/check-duplicate")
    suspend fun checkQuestionDuplicate(
        @Body request: DuplicateCheckRequest
    ): Response<DuplicateCheckResponse>

    @GET("questions/{id}")
    suspend fun getQuestion(@Path("id") id: String): Response<QAQuestionDto>

    @POST("questions/{id}/upvote")
    suspend fun upvoteQuestion(@Path("id") id: String): Response<Unit>

    @PATCH("questions/{id}")
    suspend fun updateQuestion(
        @Path("id") id: String,
        @Body request: UpdateQuestionRequest
    ): Response<QAQuestionDto>

    // ─── Answers ─────────────────────────────────────────────────────────────
    @POST("answers")
    suspend fun createAnswer(
        @Body request: CreateAnswerRequest
    ): Response<QAAnswerDto>

    @POST("answers/{id}/upvote")
    suspend fun upvoteAnswer(@Path("id") id: String): Response<Unit>

    @POST("answers/{id}/accept")
    suspend fun acceptAnswer(@Path("id") id: String): Response<Unit>

    @PATCH("answers/{id}")
    suspend fun updateAnswer(
        @Path("id") id: String,
        @Body request: UpdateAnswerRequest
    ): Response<QAAnswerDto>

    // ─── User Profile ─────────────────────────────────────────────────────────────
    @PATCH("users/me")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<UserProfileDto>

    // ─── Upload (server-side via Cloudinary) ──────────────────────────────────
    @Multipart
    @POST("upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("folder") folder: okhttp3.RequestBody? = null
    ): Response<CloudinaryUploadResponse>

    // ─── Sync (batch pull for offline support) ────────────────────────────────
    @GET("sync/flashcards")
    suspend fun syncFlashcards(
        @Query("since") since: Long
    ): Response<List<FlashcardDto>>

    @GET("sync/questions")
    suspend fun syncQuestions(
        @Query("since") since: Long
    ): Response<List<QAQuestionDto>>
}
