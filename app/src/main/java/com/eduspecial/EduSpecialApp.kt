package com.eduspecial

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.work.Configuration
import com.eduspecial.utils.NotificationScheduler
import com.eduspecial.utils.SyncWorker
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class EduSpecialApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: androidx.work.WorkerFactory
    
    @Inject
    lateinit var configRepository: com.eduspecial.data.repository.ConfigRepository
    
    @Inject
    lateinit var algoliaSearchService: com.eduspecial.data.remote.search.AlgoliaSearchService
    
    @Inject
    lateinit var notificationRepository: com.eduspecial.data.repository.NotificationRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase first
        initializeFirebase()
        
        // Then initialize other components
        SyncWorker.schedulePeriodicSync(this)
        createNotificationChannels()
        initializeRemoteConfig()
    }
    
    private fun initializeFirebase() {
        try {
            // Firebase will automatically initialize using google-services.json
            FirebaseApp.initializeApp(this)
            Log.d("EduSpecialApp", "✅ Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("EduSpecialApp", "❌ Firebase initialization failed: ${e.message}")
        }
    }
    
    private fun initializeRemoteConfig() {
        // Initialize Remote Config in background after Firebase is ready
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = configRepository.initializeConfig()
                if (success) {
                    Log.d("EduSpecialApp", "✅ Remote Config initialized successfully")
                    
                    // Initialize Algolia after Remote Config is ready
                    initializeAlgolia()
                    
                    // Initialize FCM notifications
                    initializeFCM()
                } else {
                    Log.w("EduSpecialApp", "⚠️ Remote Config initialization completed with warnings")
                }
            } catch (e: Exception) {
                Log.e("EduSpecialApp", "❌ Failed to initialize Remote Config: ${e.message}")
            }
        }
    }
    
    private suspend fun initializeAlgolia() {
        try {
            val success = algoliaSearchService.initialize()
            if (success) {
                Log.d("EduSpecialApp", "✅ Algolia Search initialized successfully")
            } else {
                Log.w("EduSpecialApp", "⚠️ Algolia Search disabled (no config)")
            }
        } catch (e: Exception) {
            Log.e("EduSpecialApp", "❌ Failed to initialize Algolia: ${e.message}")
        }
    }
    
    private fun initializeFCM() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = notificationRepository.initializeFCMToken()
                if (token != null) {
                    Log.d("EduSpecialApp", "✅ FCM initialized successfully")
                    
                    // Subscribe to default topics
                    notificationRepository.subscribeToTopic("general")
                    notificationRepository.subscribeToTopic("new_content")
                } else {
                    Log.w("EduSpecialApp", "⚠️ FCM token not available")
                }
            } catch (e: Exception) {
                Log.e("EduSpecialApp", "❌ Failed to initialize FCM: ${e.message}")
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationScheduler.CHANNEL_ID,
                "تذكير المراجعة",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات يومية لتذكيرك بمراجعة البطاقات"
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
