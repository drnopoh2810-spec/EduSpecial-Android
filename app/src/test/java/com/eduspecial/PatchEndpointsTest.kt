package com.eduspecial

import com.eduspecial.data.remote.dto.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.eduspecial.data.remote.api.EduSpecialApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

/**
 * 22.2 — MockWebServer tests for all four PATCH endpoints
 * verifying request shape and response mapping.
 */
class PatchEndpointsTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: EduSpecialApiService
    private val gson = Gson()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EduSpecialApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun patchFlashcard_sendsCorrectRequestShape() = runBlocking {
        // Arrange
        val responseBody = """
            {
                "id": "fc1",
                "term": "ABA",
                "definition": "Applied Behavior Analysis",
                "category": "ABA_THERAPY",
                "mediaUrl": null,
                "mediaType": "NONE",
                "contributor": "user1",
                "createdAt": 1700000000000
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

        val request = UpdateFlashcardRequest(
            term = "ABA",
            definition = "Applied Behavior Analysis",
            category = "ABA_THERAPY",
            mediaUrl = null,
            mediaType = "NONE"
        )

        // Act
        val response = api.updateFlashcard("fc1", request)

        // Assert request shape
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("PATCH", recordedRequest.method)
        assertTrue(recordedRequest.path!!.contains("flashcards/fc1"))
        val body = gson.fromJson(recordedRequest.body.readUtf8(), Map::class.java)
        assertEquals("ABA", body["term"])
        assertEquals("Applied Behavior Analysis", body["definition"])

        // Assert response mapping
        assertTrue(response.isSuccessful)
        assertEquals("fc1", response.body()?.id)
    }

    @Test
    fun patchQuestion_sendsCorrectRequestShape() = runBlocking {
        val responseBody = """
            {
                "id": "q1",
                "question": "What is ABA?",
                "category": "ABA_THERAPY",
                "contributor": "user1",
                "upvotes": 0,
                "createdAt": 1700000000000,
                "isAnswered": false,
                "tags": [],
                "answers": []
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

        val request = UpdateQuestionRequest(
            question = "What is ABA?",
            category = "ABA_THERAPY"
        )

        val response = api.updateQuestion("q1", request)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("PATCH", recordedRequest.method)
        assertTrue(recordedRequest.path!!.contains("questions/q1"))
        val body = gson.fromJson(recordedRequest.body.readUtf8(), Map::class.java)
        assertEquals("What is ABA?", body["question"])

        assertTrue(response.isSuccessful)
        assertEquals("q1", response.body()?.id)
    }

    @Test
    fun patchAnswer_sendsCorrectRequestShape() = runBlocking {
        val responseBody = """
            {
                "id": "a1",
                "questionId": "q1",
                "content": "ABA stands for Applied Behavior Analysis",
                "contributor": "user1",
                "upvotes": 0,
                "isAccepted": false,
                "createdAt": 1700000000000
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

        val request = UpdateAnswerRequest(content = "ABA stands for Applied Behavior Analysis")

        val response = api.updateAnswer("a1", request)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("PATCH", recordedRequest.method)
        assertTrue(recordedRequest.path!!.contains("answers/a1"))
        val body = gson.fromJson(recordedRequest.body.readUtf8(), Map::class.java)
        assertEquals("ABA stands for Applied Behavior Analysis", body["content"])

        assertTrue(response.isSuccessful)
        assertEquals("a1", response.body()?.id)
    }

    @Test
    fun patchProfile_sendsCorrectRequestShape() = runBlocking {
        val responseBody = """
            {
                "id": "user1",
                "displayName": "Ahmed",
                "email": "ahmed@example.com",
                "avatarUrl": null,
                "contributionCount": 5
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

        val request = UpdateProfileRequest(displayName = "Ahmed")

        val response = api.updateProfile(request)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("PATCH", recordedRequest.method)
        assertTrue(recordedRequest.path!!.contains("users/me"))
        val body = gson.fromJson(recordedRequest.body.readUtf8(), Map::class.java)
        assertEquals("Ahmed", body["displayName"])

        assertTrue(response.isSuccessful)
        assertEquals("Ahmed", response.body()?.displayName)
    }
}
