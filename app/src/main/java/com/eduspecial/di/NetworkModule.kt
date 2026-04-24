package com.eduspecial.di

import com.eduspecial.data.remote.api.EduSpecialApiService
import com.eduspecial.data.remote.config.RemoteConfigManager
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Wires Retrofit to the embedded backend URL exposed by RemoteConfigManager
 * (defaults to the Flask admin backend in /myproject). Attaches the Firebase
 * ID token to every outgoing request so the backend can authenticate the user.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideAuthInterceptor(auth: FirebaseAuth): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            try {
                val token = runBlocking { auth.currentUser?.getIdToken(false)?.await()?.token }
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $token")
                }
            } catch (_: Exception) { /* offline / not signed in — proceed unauthenticated */ }
            addHeader("Accept", "application/json")
        }.build()
        chain.proceed(request)
    }

    @Provides @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        remoteConfigManager: RemoteConfigManager
    ): Retrofit {
        val baseUrl = remoteConfigManager.getBackendBaseUrl()
            .let { if (it.endsWith("/")) it else "$it/" }
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides @Singleton
    fun provideEduSpecialApiService(retrofit: Retrofit): EduSpecialApiService =
        retrofit.create(EduSpecialApiService::class.java)
}
