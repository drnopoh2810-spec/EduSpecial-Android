package com.eduspecial

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.work.Configuration
import com.eduspecial.data.remote.secure.RuntimeConfigProvider
import com.eduspecial.utils.NotificationScheduler
import com.eduspecial.utils.SyncWorker
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Application bootstrap.
 *
 *   1) Fetch the encrypted runtime config (or load from cache) — synchronous
 *      block, because Firebase MUST be initialized before any singleton uses it.
 *   2) Initialize Firebase manually with [FirebaseOptions] built from the
 *      runtime config — so we ship NO google-services.json in the APK.
 *   3) Initialize the rest (Cloudinary, Algolia, FCM) in the background.
 */
@HiltAndroidApp
class EduSpecialApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: androidx.work.WorkerFactory
    @Inject lateinit var runtimeConfigProvider: RuntimeConfigProvider
    @Inject lateinit var configRepository: com.eduspecial.data.repository.ConfigRepository
    @Inject lateinit var algoliaSearchService: com.eduspecial.data.remote.search.AlgoliaSearchService
    @Inject lateinit var notificationRepository: com.eduspecial.data.repository.NotificationRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        bootstrapRuntimeAndFirebase()
        SyncWorker.schedulePeriodicSync(this)
        createNotificationChannels()
        initializeBackgroundServices()
    }

    /**
     * Synchronous bootstrap: pull cached/remote config and stand up Firebase.
     * This blocks `onCreate` for at most a few hundred milliseconds when the
     * cache is warm; on first-ever launch with no internet it will fail and
     * we surface that via [configRepository.configStatus].
     */
    private fun bootstrapRuntimeAndFirebase() = runBlocking {
        try {
            val ok = runtimeConfigProvider.bootstrap()
            if (!ok) {
                Log.e("EduSpecialApp", "❌ Runtime config unavailable — Firebase NOT initialized")
                return@runBlocking
            }
            val fb = runtimeConfigProvider.current?.firebase
                ?: error("runtime config missing firebase block")

            if (FirebaseApp.getApps(this@EduSpecialApp).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId(fb.applicationId)
                    .setApiKey(fb.apiKey)
                    .setProjectId(fb.projectId)
                    .setGcmSenderId(fb.projectNumber)
                    .setStorageBucket(fb.storageBucket)
                    .apply { if (fb.databaseUrl.isNotBlank()) setDatabaseUrl(fb.databaseUrl) }
                    .build()
                FirebaseApp.initializeApp(this@EduSpecialApp, options)
                Log.d("EduSpecialApp", "✅ Firebase initialized from secure runtime config")
            }
        } catch (e: Exception) {
            Log.e("EduSpecialApp", "❌ bootstrap failed: ${e.message}", e)
        }
    }

    private fun initializeBackgroundServices() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                configRepository.initializeConfig()
                algoliaSearchService.initialize()
                notificationRepository.initializeFCMToken()?.let {
                    notificationRepository.subscribeToTopic("general")
                    notificationRepository.subscribeToTopic("new_content")
                }
            } catch (e: Exception) {
                Log.e("EduSpecialApp", "❌ background init: ${e.message}")
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationScheduler.CHANNEL_ID,
                "تذكير المراجعة",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "إشعارات يومية لتذكيرك بمراجعة البطاقات" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
