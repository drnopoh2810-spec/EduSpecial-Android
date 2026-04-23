package com.eduspecial.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * OkHttp interceptor that retries failed requests with exponential backoff.
 *
 * Retries on:
 *  - Network errors (IOException, SocketTimeoutException)
 *  - Server errors: 429 Too Many Requests, 502/503/504 (backend overload)
 *
 * Does NOT retry on:
 *  - 4xx client errors (except 429)
 *  - Successful responses
 *
 * Backoff: 1s → 2s → 4s (max [maxRetries] attempts)
 * Respects Retry-After header from 429 responses.
 */
class RetryInterceptor(
    private val maxRetries: Int = 3
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null
        var response: Response? = null

        for (attempt in 0..maxRetries) {
            // Release previous failed response body to avoid leaks
            response?.close()

            try {
                response = chain.proceed(request)

                when {
                    response.isSuccessful -> return response

                    response.code == 429 -> {
                        // Respect Retry-After header if present
                        val retryAfterSec = response.header("Retry-After")?.toLongOrNull() ?: (1L shl attempt)
                        Log.w("RetryInterceptor", "429 rate limited — waiting ${retryAfterSec}s (attempt $attempt)")
                        if (attempt < maxRetries) Thread.sleep(retryAfterSec * 1000)
                    }

                    response.code in listOf(502, 503, 504) -> {
                        val backoffMs = (1L shl attempt) * 1000  // 1s, 2s, 4s
                        Log.w("RetryInterceptor", "Server error ${response.code} — backoff ${backoffMs}ms (attempt $attempt)")
                        if (attempt < maxRetries) Thread.sleep(backoffMs)
                    }

                    else -> return response  // 4xx or other — don't retry
                }

            } catch (e: SocketTimeoutException) {
                lastException = e
                val backoffMs = (1L shl attempt) * 1000
                Log.w("RetryInterceptor", "Timeout — backoff ${backoffMs}ms (attempt $attempt)")
                if (attempt < maxRetries) Thread.sleep(backoffMs)

            } catch (e: IOException) {
                lastException = e
                val backoffMs = (1L shl attempt) * 1000
                Log.w("RetryInterceptor", "IO error — backoff ${backoffMs}ms (attempt $attempt)")
                if (attempt < maxRetries) Thread.sleep(backoffMs)
            }
        }

        // All retries exhausted
        return response ?: throw lastException ?: IOException("Request failed after $maxRetries retries")
    }
}
