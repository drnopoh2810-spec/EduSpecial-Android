package com.eduspecial.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.data.repository.QARepository
import com.eduspecial.data.repository.BookmarkRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Background worker that syncs Firestore → Room cache.
 *
 * Since we now use Firestore directly, this worker is much simpler:
 * 1. Pull latest flashcards from Firestore into Room
 * 2. Pull latest questions from Firestore into Room
 * 3. Clean up orphaned bookmarks
 *
 * Firestore handles offline automatically — this worker just keeps
 * Room in sync for the SRS queries that need local SQL.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val flashcardRepository: FlashcardRepository,
    private val qaRepository: QARepository,
    private val bookmarkRepository: BookmarkRepository,
    private val prefs: UserPreferencesDataStore,
    private val networkMonitor: NetworkMonitor,
    private val circuitBreaker: CircuitBreaker
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!networkMonitor.isCurrentlyOnline()) {
            return if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
        if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
            return if (runAttemptCount < 3) Result.retry() else Result.failure()
        }

        return try {
            val lastSync = prefs.lastSyncTimestamp.first()

            // Pull incremental updates from Firestore into Room
            flashcardRepository.syncFromServer(since = lastSync)
            qaRepository.syncFromServer(since = lastSync)

            // Clean up orphaned bookmarks
            bookmarkRepository.removeOrphans(emptyList())

            prefs.updateLastSync()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "EduSpecial_SyncWorker"
        const val IMMEDIATE_WORK_NAME = "EduSpecial_ImmediateSync"

        fun schedulePeriodicSync(context: Context) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request
            )
        }

        fun triggerImmediateSync(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_WORK_NAME, ExistingWorkPolicy.REPLACE, request
            )
        }
    }
}
