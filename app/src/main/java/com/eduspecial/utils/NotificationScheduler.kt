package com.eduspecial.utils

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val REMINDER_WORK_NAME = "EduSpecial_StudyReminder"
        const val CHANNEL_ID = "study_reminder_channel"
    }

    fun schedule(enabled: Boolean, reminderTimeMillis: Long) {
        if (!enabled) {
            WorkManager.getInstance(context).cancelUniqueWork(REMINDER_WORK_NAME)
            return
        }
        val delay = calculateDelayUntilNextOccurrence(reminderTimeMillis)
        val request = OneTimeWorkRequestBuilder<StudyReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            REMINDER_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private fun calculateDelayUntilNextOccurrence(reminderTimeMillis: Long): Long {
        val now = System.currentTimeMillis()
        // reminderTimeMillis is the time-of-day in millis from midnight (e.g., 8*60*60*1000 for 8am)
        val todayMidnight = now - (now % (24 * 60 * 60 * 1000L))
        var nextOccurrence = todayMidnight + reminderTimeMillis
        if (nextOccurrence <= now) {
            nextOccurrence += 24 * 60 * 60 * 1000L // schedule for tomorrow
        }
        return nextOccurrence - now
    }
}
