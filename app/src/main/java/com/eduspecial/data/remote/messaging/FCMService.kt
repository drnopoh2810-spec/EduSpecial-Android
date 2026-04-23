package com.eduspecial.data.remote.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.eduspecial.MainActivity
import com.eduspecial.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: com.eduspecial.data.repository.NotificationRepository

    companion object {
        private const val TAG = "FCMService"
        const val CHANNEL_ID_GENERAL = "general_notifications"
        const val CHANNEL_ID_REMINDERS = "study_reminders"
        const val CHANNEL_ID_CONTENT = "new_content"
        const val CHANNEL_ID_SOCIAL = "social_interactions"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "🔑 New FCM token received: ${token.take(20)}...")
        
        // Save token to repository for backend use
        notificationRepository.updateFCMToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "📨 FCM message received from: ${remoteMessage.from}")
        
        // Handle data payload (always present, even when app is in background)
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "📊 Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Handle notification payload (only when app is in foreground)
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "📱 Message notification: ${notification.title}")
            showNotification(
                title = notification.title ?: "EduSpecial",
                body = notification.body ?: "",
                data = remoteMessage.data
            )
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return
        
        when (type) {
            "study_reminder" -> handleStudyReminder(data)
            "new_flashcard" -> handleNewContent(data, "بطاقة جديدة")
            "new_question" -> handleNewContent(data, "سؤال جديد")
            "answer_received" -> handleAnswerReceived(data)
            "upvote_received" -> handleUpvoteReceived(data)
            "achievement_unlocked" -> handleAchievement(data)
            else -> Log.w(TAG, "⚠️ Unknown notification type: $type")
        }
    }

    private fun handleStudyReminder(data: Map<String, String>) {
        val dueCount = data["due_count"]?.toIntOrNull() ?: 0
        showNotification(
            title = "⏰ وقت المراجعة!",
            body = "لديك $dueCount بطاقة جاهزة للمراجعة",
            channelId = CHANNEL_ID_REMINDERS,
            data = data
        )
    }

    private fun handleNewContent(data: Map<String, String>, contentType: String) {
        val title = data["content_title"] ?: contentType
        val category = data["category"] ?: ""
        
        showNotification(
            title = "🆕 $contentType متاح",
            body = "$title في فئة $category",
            channelId = CHANNEL_ID_CONTENT,
            data = data
        )
    }

    private fun handleAnswerReceived(data: Map<String, String>) {
        val questionTitle = data["question_title"] ?: "سؤالك"
        val answerAuthor = data["answer_author"] ?: "أحد المستخدمين"
        
        showNotification(
            title = "💬 إجابة جديدة!",
            body = "$answerAuthor أجاب على: $questionTitle",
            channelId = CHANNEL_ID_SOCIAL,
            data = data
        )
    }

    private fun handleUpvoteReceived(data: Map<String, String>) {
        val contentType = data["content_type"] ?: "محتواك"
        val upvoteCount = data["upvote_count"] ?: "1"
        
        showNotification(
            title = "👍 إعجاب جديد!",
            body = "حصل $contentType على $upvoteCount إعجاب",
            channelId = CHANNEL_ID_SOCIAL,
            data = data
        )
    }

    private fun handleAchievement(data: Map<String, String>) {
        val achievementName = data["achievement_name"] ?: "إنجاز جديد"
        val points = data["points"] ?: "0"
        
        showNotification(
            title = "🏆 إنجاز مفتوح!",
            body = "$achievementName (+$points نقطة)",
            channelId = CHANNEL_ID_GENERAL,
            data = data
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        channelId: String = CHANNEL_ID_GENERAL,
        data: Map<String, String> = emptyMap()
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add navigation data
            data["target_screen"]?.let { putExtra("target_screen", it) }
            data["item_id"]?.let { putExtra("item_id", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // You'll need to add this icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        // Add action buttons based on notification type
        when (data["type"]) {
            "study_reminder" -> {
                val studyIntent = Intent(this, MainActivity::class.java).apply {
                    putExtra("target_screen", "study")
                }
                val studyPendingIntent = PendingIntent.getActivity(
                    this, 1, studyIntent, PendingIntent.FLAG_IMMUTABLE
                )
                notificationBuilder.addAction(
                    R.drawable.ic_study, "ابدأ المراجعة", studyPendingIntent
                )
            }
            "new_question" -> {
                val answerIntent = Intent(this, MainActivity::class.java).apply {
                    putExtra("target_screen", "qa")
                    putExtra("item_id", data["question_id"])
                }
                val answerPendingIntent = PendingIntent.getActivity(
                    this, 2, answerIntent, PendingIntent.FLAG_IMMUTABLE
                )
                notificationBuilder.addAction(
                    R.drawable.ic_answer, "أجب الآن", answerPendingIntent
                )
            }
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        
        Log.d(TAG, "✅ Notification shown: $title")
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_GENERAL,
                    "إشعارات عامة",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "إشعارات عامة من التطبيق"
                },
                NotificationChannel(
                    CHANNEL_ID_REMINDERS,
                    "تذكير المراجعة",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "تذكيرات يومية لمراجعة البطاقات"
                },
                NotificationChannel(
                    CHANNEL_ID_CONTENT,
                    "محتوى جديد",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "إشعارات عند إضافة محتوى جديد"
                },
                NotificationChannel(
                    CHANNEL_ID_SOCIAL,
                    "التفاعل الاجتماعي",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "إشعارات الإجابات والإعجابات"
                }
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { notificationManager.createNotificationChannel(it) }
            
            Log.d(TAG, "✅ Notification channels created")
        }
    }
}