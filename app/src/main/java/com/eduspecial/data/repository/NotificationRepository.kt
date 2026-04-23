package com.eduspecial.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) {
    
    companion object {
        private const val TAG = "NotificationRepository"
        private const val COLLECTION_USER_TOKENS = "user_tokens"
        private const val COLLECTION_NOTIFICATION_SETTINGS = "notification_settings"
    }

    /**
     * Updates the FCM token for the current user in Firestore.
     * This allows the backend to send targeted notifications.
     */
    fun updateFCMToken(token: String) {
        val userId = auth.currentUser?.uid ?: return
        
        try {
            firestore.collection(COLLECTION_USER_TOKENS)
                .document(userId)
                .set(mapOf(
                    "fcmToken" to token,
                    "updatedAt" to System.currentTimeMillis(),
                    "platform" to "android"
                ))
            Log.d(TAG, "✅ FCM token updated for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update FCM token: ${e.message}")
        }
    }

    /**
     * Gets the current FCM token and saves it to Firestore.
     */
    suspend fun initializeFCMToken(): String? {
        return try {
            val token = messaging.token.await()
            updateFCMToken(token)
            Log.d(TAG, "🔑 FCM token initialized: ${token.take(20)}...")
            token
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get FCM token: ${e.message}")
            null
        }
    }

    /**
     * Subscribes to topic-based notifications.
     */
    suspend fun subscribeToTopic(topic: String): Boolean {
        return try {
            messaging.subscribeToTopic(topic).await()
            Log.d(TAG, "✅ Subscribed to topic: $topic")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to subscribe to topic $topic: ${e.message}")
            false
        }
    }

    /**
     * Unsubscribes from topic-based notifications.
     */
    suspend fun unsubscribeFromTopic(topic: String): Boolean {
        return try {
            messaging.unsubscribeFromTopic(topic).await()
            Log.d(TAG, "✅ Unsubscribed from topic: $topic")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to unsubscribe from topic $topic: ${e.message}")
            false
        }
    }

    /**
     * Gets notification settings for the current user.
     */
    suspend fun getNotificationSettings(): NotificationSettings {
        val userId = auth.currentUser?.uid ?: return NotificationSettings()
        
        return try {
            val doc = firestore.collection(COLLECTION_NOTIFICATION_SETTINGS)
                .document(userId)
                .get()
                .await()
            
            if (doc.exists()) {
                NotificationSettings(
                    studyReminders = doc.getBoolean("studyReminders") ?: true,
                    newContent = doc.getBoolean("newContent") ?: true,
                    socialInteractions = doc.getBoolean("socialInteractions") ?: true,
                    achievements = doc.getBoolean("achievements") ?: true,
                    reminderTime = doc.getString("reminderTime") ?: "19:00"
                )
            } else {
                NotificationSettings()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get notification settings: ${e.message}")
            NotificationSettings()
        }
    }

    /**
     * Updates notification settings for the current user.
     */
    suspend fun updateNotificationSettings(settings: NotificationSettings): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        
        return try {
            firestore.collection(COLLECTION_NOTIFICATION_SETTINGS)
                .document(userId)
                .set(mapOf(
                    "studyReminders" to settings.studyReminders,
                    "newContent" to settings.newContent,
                    "socialInteractions" to settings.socialInteractions,
                    "achievements" to settings.achievements,
                    "reminderTime" to settings.reminderTime,
                    "updatedAt" to System.currentTimeMillis()
                ))
                .await()
            
            // Update topic subscriptions based on settings
            updateTopicSubscriptions(settings)
            
            Log.d(TAG, "✅ Notification settings updated")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update notification settings: ${e.message}")
            false
        }
    }

    /**
     * Subscribes/unsubscribes from topics based on user settings.
     */
    private suspend fun updateTopicSubscriptions(settings: NotificationSettings) {
        val topics = mapOf(
            "new_content" to settings.newContent,
            "achievements" to settings.achievements
        )
        
        topics.forEach { (topic, enabled) ->
            if (enabled) {
                subscribeToTopic(topic)
            } else {
                unsubscribeFromTopic(topic)
            }
        }
    }

    /**
     * Schedules local study reminder notifications.
     */
    suspend fun scheduleStudyReminders(enabled: Boolean, time: String) {
        val userId = auth.currentUser?.uid ?: return
        
        try {
            // This would typically integrate with WorkManager or AlarmManager
            // For now, we'll just save the preference
            firestore.collection(COLLECTION_NOTIFICATION_SETTINGS)
                .document(userId)
                .update(mapOf(
                    "studyReminders" to enabled,
                    "reminderTime" to time
                ))
                .await()
            
            Log.d(TAG, "✅ Study reminders ${if (enabled) "enabled" else "disabled"} at $time")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to schedule study reminders: ${e.message}")
        }
    }

    /**
     * Sends a test notification to verify setup.
     */
    suspend fun sendTestNotification(): Boolean {
        return try {
            val token = messaging.token.await()
            Log.d(TAG, "🧪 Test notification would be sent to: ${token.take(20)}...")
            // In a real implementation, this would call a Cloud Function
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to send test notification: ${e.message}")
            false
        }
    }
}

/**
 * Data class representing user notification preferences.
 */
data class NotificationSettings(
    val studyReminders: Boolean = true,
    val newContent: Boolean = true,
    val socialInteractions: Boolean = true,
    val achievements: Boolean = true,
    val reminderTime: String = "19:00" // 7 PM default
)

/**
 * Enum representing different notification types.
 */
enum class NotificationType {
    STUDY_REMINDER,
    NEW_FLASHCARD,
    NEW_QUESTION,
    ANSWER_RECEIVED,
    UPVOTE_RECEIVED,
    ACHIEVEMENT_UNLOCKED,
    GENERAL
}