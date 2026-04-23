package com.eduspecial.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.eduspecial.MainActivity
import com.eduspecial.data.repository.AnalyticsRepository
import com.eduspecial.data.repository.FlashcardRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class StudyReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val flashcardRepository: FlashcardRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val prefs: UserPreferencesDataStore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Check if notifications are enabled
        val notificationsEnabled = prefs.studyNotificationsEnabled.first()
        if (!notificationsEnabled) return Result.success()

        // Check if daily goal is already met
        val dailyGoal = prefs.dailyGoal.first()
        val todayReviewed = analyticsRepository.getTodayReviewCount()
        if (todayReviewed >= dailyGoal) return Result.success()

        // Get due card count
        val dueCount = flashcardRepository.getDueCount()

        // Show notification
        showStudyReminderNotification(dueCount)

        // Re-schedule for tomorrow
        val reminderTime = prefs.reminderTimeMillis.first()
        NotificationScheduler(context).schedule(enabled = true, reminderTimeMillis = reminderTime)

        return Result.success()
    }

    private fun showStudyReminderNotification(dueCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "study")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val body = if (dueCount > 0) {
            "لديك $dueCount بطاقة للمراجعة اليوم"
        } else {
            "حافظ على تقدمك وراجع بطاقاتك اليوم"
        }

        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("حان وقت المراجعة!")
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 1001
    }
}
