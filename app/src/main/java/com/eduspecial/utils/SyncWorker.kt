package com.eduspecial.utils

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.eduspecial.data.local.dao.PendingSubmissionDao
import com.eduspecial.data.local.entities.PendingSubmissionEntity
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.data.repository.QARepository
import com.eduspecial.data.repository.BookmarkRepository
import com.eduspecial.domain.model.FlashcardCategory
import com.eduspecial.domain.model.MediaType
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val flashcardRepository: FlashcardRepository,
    private val qaRepository: QARepository,
    private val bookmarkRepository: BookmarkRepository,
    private val pendingDao: PendingSubmissionDao,
    private val prefs: UserPreferencesDataStore,
    private val networkMonitor: NetworkMonitor,
    private val circuitBreaker: CircuitBreaker
) : CoroutineWorker(context, workerParams) {

    private val gson = Gson()

    override suspend fun doWork(): Result {
        if (!networkMonitor.isCurrentlyOnline()) {
            return if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
        if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
            return if (runAttemptCount < 3) Result.retry() else Result.failure()
        }

        return try {
            // 1. Process Pending Submissions (Local -> Server)
            val pending = pendingDao.getAll()
            for (submission in pending) {
                val isSuccess = try {
                    when (submission.type) {
                        PendingSubmissionEntity.TYPE_FLASHCARD -> {
                            val data = gson.fromJson(submission.payload, FlashcardCreatePayload::class.java)
                            flashcardRepository.createFlashcard(
                                term = data.term,
                                definition = data.definition,
                                category = FlashcardCategory.valueOf(data.category),
                                mediaUrl = data.mediaUrl,
                                mediaType = MediaType.valueOf(data.mediaType),
                                contributorId = data.contributorId
                            ).isSuccess
                        }
                        PendingSubmissionEntity.TYPE_FLASHCARD_EDIT -> {
                            val data = gson.fromJson(submission.payload, FlashcardEditPayload::class.java)
                            flashcardRepository.editFlashcard(
                                id = data.id,
                                term = data.term,
                                definition = data.definition,
                                category = FlashcardCategory.valueOf(data.category),
                                mediaUrl = data.mediaUrl,
                                mediaType = MediaType.valueOf(data.mediaType)
                            ).isSuccess
                        }
                        PendingSubmissionEntity.TYPE_QUESTION -> {
                            val data = gson.fromJson(submission.payload, QuestionCreatePayload::class.java)
                            qaRepository.createQuestion(
                                question = data.question,
                                category = FlashcardCategory.valueOf(data.category),
                                contributorId = data.contributorId,
                                tags = data.tags
                            ).isSuccess
                        }
                        PendingSubmissionEntity.TYPE_ANSWER -> {
                            val data = gson.fromJson(submission.payload, AnswerCreatePayload::class.java)
                            qaRepository.createAnswer(
                                questionId = data.questionId,
                                content = data.content,
                                contributorId = data.contributorId
                            ).isSuccess
                        }
                        else -> true
                    }
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Error processing submission ${submission.localId}", e)
                    false
                }

                if (isSuccess) {
                    pendingDao.delete(submission)
                } else {
                    pendingDao.incrementRetry(submission.localId)
                }
            }
            pendingDao.deleteFailedSubmissions()

            // 2. Pull incremental updates from Firestore (Server -> Local)
            val lastSync = prefs.lastSyncTimestamp.first()
            flashcardRepository.syncFromServer(since = lastSync)
            qaRepository.syncFromServer(since = lastSync)

            // 3. Clean up orphaned bookmarks
            bookmarkRepository.removeOrphans(emptyList())

            prefs.updateLastSync()
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    // Payload classes moved out of helper functions to avoid KSP issues with mangled suspend signatures
    private data class FlashcardCreatePayload(
        val term: String, val definition: String, val category: String,
        val mediaUrl: String?, val mediaType: String, val contributorId: String
    )
    private data class FlashcardEditPayload(
        val id: String, val term: String, val definition: String,
        val category: String, val mediaUrl: String?, val mediaType: String
    )
    private data class QuestionCreatePayload(
        val question: String, val category: String, val contributorId: String, val tags: List<String>
    )
    private data class AnswerCreatePayload(
        val questionId: String, val content: String, val contributorId: String
    )

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
