package com.eduspecial.domain.usecase

import android.net.Uri
import com.eduspecial.data.remote.api.CloudinaryService
import com.eduspecial.data.remote.api.UploadResult
import com.eduspecial.data.repository.AnalyticsRepository
import com.eduspecial.data.repository.AuthRepository
import com.eduspecial.data.repository.BookmarkRepository
import com.eduspecial.data.repository.DuplicateCheckResult
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.data.repository.QARepository
import com.eduspecial.domain.model.*
import com.eduspecial.utils.NotificationScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ─── Flashcard Use Cases ──────────────────────────────────────────────────────

class GetStudyQueueUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    operator fun invoke(): Flow<List<Flashcard>> = repository.getStudyQueue()
}

class GetFlashcardsUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    operator fun invoke(category: FlashcardCategory? = null): Flow<List<Flashcard>> =
        if (category != null) repository.getByCategory(category)
        else repository.getAllFlashcards()
}

class CheckDuplicateTermUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(term: String): DuplicateCheckResult =
        repository.checkDuplicate(term)
}

class SubmitFlashcardUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(
        term: String,
        definition: String,
        category: FlashcardCategory,
        mediaUrl: String?,
        mediaType: MediaType,
        contributorId: String
    ): Result<Flashcard> = repository.createFlashcard(
        term, definition, category, mediaUrl, mediaType, contributorId
    )
}

class ProcessSRSReviewUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(flashcard: Flashcard, result: SRSResult) =
        repository.processReview(flashcard, result)
}

// ─── Q&A Use Cases ────────────────────────────────────────────────────────────

class GetQuestionsUseCase @Inject constructor(
    private val repository: QARepository
) {
    operator fun invoke(unansweredOnly: Boolean = false): Flow<List<QAQuestion>> =
        if (unansweredOnly) repository.getUnansweredQuestions()
        else repository.getAllQuestions()
}

class SubmitQuestionUseCase @Inject constructor(
    private val repository: QARepository
) {
    suspend operator fun invoke(
        question: String,
        category: FlashcardCategory,
        contributorId: String,
        tags: List<String> = emptyList()
    ): Result<QAQuestion> = repository.createQuestion(question, category, contributorId, tags)
}

class SubmitAnswerUseCase @Inject constructor(
    private val repository: QARepository
) {
    suspend operator fun invoke(
        questionId: String,
        content: String,
        contributorId: String
    ): Result<QAAnswer> = repository.createAnswer(questionId, content, contributorId)
}

class CheckDuplicateQuestionUseCase @Inject constructor(
    private val repository: QARepository
) {
    suspend operator fun invoke(question: String): DuplicateCheckResult =
        repository.checkDuplicate(question)
}

// ─── Edit Use Cases ───────────────────────────────────────────────────────────

class EditFlashcardUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(
        id: String,
        term: String,
        definition: String,
        category: FlashcardCategory,
        mediaUrl: String?,
        mediaType: MediaType
    ): Result<Flashcard> = repository.editFlashcard(id, term, definition, category, mediaUrl, mediaType)
}

class EditQuestionUseCase @Inject constructor(
    private val repository: QARepository
) {
    suspend operator fun invoke(
        id: String,
        question: String,
        category: FlashcardCategory
    ): Result<QAQuestion> = repository.editQuestion(id, question, category)
}

class EditAnswerUseCase @Inject constructor(
    private val repository: QARepository
) {
    suspend operator fun invoke(id: String, content: String): Result<QAAnswer> =
        repository.editAnswer(id, content)
}

class AcceptAnswerUseCase @Inject constructor(
    private val repository: QARepository
) {
    suspend operator fun invoke(answerId: String, questionId: String): Result<Unit> =
        repository.acceptAnswer(answerId, questionId)
}

class UpvoteAnswerUseCase @Inject constructor(
    private val repository: QARepository
) {
    suspend operator fun invoke(answerId: String): Result<Unit> =
        repository.upvoteAnswer(answerId)
}

// ─── Bookmark Use Cases ───────────────────────────────────────────────────────

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(itemId: String, itemType: BookmarkType): Boolean =
        repository.toggle(itemId, itemType)
}

class GetBookmarksUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<BookmarkCollection> = repository.getAllBookmarks()
}

// ─── Analytics Use Cases ──────────────────────────────────────────────────────

class RecordReviewUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(archivedCount: Int = 0) =
        repository.recordReview(reviewCount = 1, archivedCount = archivedCount)
}

class GetStudyStreakUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(): Int = repository.getStreak()
}

class GetWeeklyProgressUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(): List<DailyProgress> = repository.getLast7Days()
}

class GetCategoryMasteryUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(): List<CategoryMastery> = repository.getCategoryMastery()
}

// ─── Profile Use Cases ────────────────────────────────────────────────────────

class UpdateDisplayNameUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(displayName: String): Result<Unit> {
        if (displayName.length < 2 || displayName.length > 50) {
            return Result.failure(IllegalArgumentException("يجب أن يكون الاسم بين 2 و 50 حرفاً"))
        }
        return repository.updateDisplayName(displayName)
    }
}

class UploadAvatarUseCase @Inject constructor(
    private val cloudinary: CloudinaryService,
    private val repository: AuthRepository
) {
    suspend operator fun invoke(uri: Uri): Result<String> {
        return try {
            val uploadResult = cloudinary.uploadMedia(
                uri = uri,
                resourceType = "image",
                folder = "avatars"
                // onProgress not needed here — no progress bar for avatar upload
            )
            when (uploadResult) {
                is UploadResult.Success -> {
                    repository.updateAvatarUrl(uploadResult.url)
                    Result.success(uploadResult.url)
                }
                is UploadResult.Failure -> Result.failure(Exception(uploadResult.error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ─── Notification Use Cases ───────────────────────────────────────────────────

class ScheduleStudyReminderUseCase @Inject constructor(
    private val scheduler: NotificationScheduler
) {
    operator fun invoke(enabled: Boolean, timeMillis: Long) =
        scheduler.schedule(enabled, timeMillis)
}
