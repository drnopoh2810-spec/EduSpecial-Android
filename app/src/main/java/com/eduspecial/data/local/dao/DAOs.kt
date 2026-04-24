package com.eduspecial.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.eduspecial.data.local.entities.BookmarkEntity
import com.eduspecial.data.local.entities.DailyReviewLogEntity
import com.eduspecial.data.local.entities.FlashcardEntity
import com.eduspecial.data.local.entities.PendingSubmissionEntity
import com.eduspecial.data.local.entities.QAAnswerEntity
import com.eduspecial.data.local.entities.QAQuestionEntity
import kotlinx.coroutines.flow.Flow

data class CategoryMasteryRow(
    val category: String,
    val total: Int,
    val archived: Int
)

// ─── Flashcard DAO ────────────────────────────────────────────────────────────
@Dao
interface FlashcardDao {

    @Query("SELECT * FROM flashcards ORDER BY createdAt DESC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    /** Paging 3 source — returns pages of flashcards for scalable list loading */
    @Query("SELECT * FROM flashcards ORDER BY createdAt DESC")
    fun getFlashcardsPaged(): PagingSource<Int, FlashcardEntity>

    /** Paging 3 source filtered by category */
    @Query("SELECT * FROM flashcards WHERE category = :category ORDER BY createdAt DESC")
    fun getFlashcardsPagedByCategory(category: String): PagingSource<Int, FlashcardEntity>

    /** Delete non-pending flashcards for a category (used by RemoteMediator on refresh) */
    @Query("DELETE FROM flashcards WHERE category = :category AND isPendingSync = 0")
    suspend fun deleteByCategoryIfNotPending(category: String)

    /** Delete all non-pending flashcards (used by RemoteMediator on full refresh) */
    @Query("DELETE FROM flashcards WHERE isPendingSync = 0")
    suspend fun deleteAllNotPending()

    @Query("SELECT * FROM flashcards WHERE reviewState IN ('NEW', 'LEARNING', 'REVIEW') ORDER BY nextReviewDate ASC")
    fun getStudyQueue(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE reviewState = 'ARCHIVED'")
    fun getArchivedFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun getFlashcardById(id: String): FlashcardEntity?

    @Query("SELECT COUNT(*) FROM flashcards WHERE LOWER(term) = LOWER(:term)")
    suspend fun countByTerm(term: String): Int

    @Query("SELECT * FROM flashcards WHERE category = :category")
    fun getByCategory(category: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE isPendingSync = 1")
    suspend fun getPendingSync(): List<FlashcardEntity>

    /**
     * Full-text local search — searches term and definition.
     * Used as instant fallback when Algolia is unavailable or slow.
     */
    @Query("""
        SELECT * FROM flashcards
        WHERE LOWER(term) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(definition) LIKE '%' || LOWER(:query) || '%'
        ORDER BY
            CASE WHEN LOWER(term) LIKE LOWER(:query) || '%' THEN 0 ELSE 1 END,
            createdAt DESC
        LIMIT 30
    """)
    suspend fun searchFlashcards(query: String): List<FlashcardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flashcards: List<FlashcardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flashcard: FlashcardEntity)

    @Update
    suspend fun update(flashcard: FlashcardEntity)

    @Query("UPDATE flashcards SET reviewState = :state, easeFactor = :easeFactor, interval = :interval, nextReviewDate = :nextReviewDate WHERE id = :id")
    suspend fun updateReviewState(id: String, state: String, easeFactor: Float, interval: Int, nextReviewDate: Long)

    @Query("UPDATE flashcards SET term = :term, definition = :definition, category = :category, mediaUrl = :mediaUrl, mediaType = :mediaType WHERE id = :id")
    suspend fun updateContent(id: String, term: String, definition: String, category: String, mediaUrl: String?, mediaType: String)

    @Query("""
        SELECT category,
               COUNT(*) AS total,
               SUM(CASE WHEN reviewState = 'ARCHIVED' THEN 1 ELSE 0 END) AS archived
        FROM flashcards
        GROUP BY category
    """)
    suspend fun getCategoryMastery(): List<CategoryMasteryRow>

    @Query("SELECT COUNT(*) FROM flashcards WHERE nextReviewDate <= :now AND reviewState IN ('NEW', 'LEARNING', 'REVIEW')")
    suspend fun getDueCount(now: Long = System.currentTimeMillis()): Int

    @Delete
    suspend fun delete(flashcard: FlashcardEntity)

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun count(): Int
}

// ─── Q&A DAO ─────────────────────────────────────────────────────────────────
@Dao
interface QADao {

    @Query("SELECT * FROM qa_questions ORDER BY createdAt DESC")
    fun getAllQuestions(): Flow<List<QAQuestionEntity>>

    @Query("SELECT * FROM qa_questions WHERE isAnswered = 0 ORDER BY upvotes DESC")
    fun getUnansweredQuestions(): Flow<List<QAQuestionEntity>>

    @Query("SELECT * FROM qa_answers WHERE questionId = :questionId ORDER BY isAccepted DESC, upvotes DESC")
    fun getAnswersForQuestion(questionId: String): Flow<List<QAAnswerEntity>>

    @Query("SELECT COUNT(*) FROM qa_questions WHERE LOWER(question) = LOWER(:question)")
    suspend fun countByQuestion(question: String): Int

    /**
     * Full-text local search on questions.
     */
    @Query("""
        SELECT * FROM qa_questions
        WHERE LOWER(question) LIKE '%' || LOWER(:query) || '%'
        ORDER BY upvotes DESC, createdAt DESC
        LIMIT 20
    """)
    suspend fun searchQuestions(query: String): List<QAQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QAQuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QAQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: QAAnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<QAAnswerEntity>)

    @Query("UPDATE qa_questions SET upvotes = upvotes + 1 WHERE id = :id")
    suspend fun upvoteQuestion(id: String)

    @Query("UPDATE qa_answers SET upvotes = upvotes + 1 WHERE id = :id")
    suspend fun upvoteAnswer(id: String)

    @Query("SELECT * FROM qa_questions WHERE isPendingSync = 1")
    suspend fun getPendingSync(): List<QAQuestionEntity>

    @Query("UPDATE qa_questions SET question = :question, category = :category WHERE id = :id")
    suspend fun updateQuestion(id: String, question: String, category: String)

    @Query("UPDATE qa_answers SET content = :content WHERE id = :id")
    suspend fun updateAnswer(id: String, content: String)

    @Query("UPDATE qa_answers SET isAccepted = 1 WHERE id = :id")
    suspend fun acceptAnswer(id: String)

    @Query("UPDATE qa_questions SET isAnswered = 1 WHERE id = :questionId")
    suspend fun markQuestionAnswered(questionId: String)
}

// ─── Pending Submissions DAO ──────────────────────────────────────────────────
@Dao
interface PendingSubmissionDao {

    @Query("SELECT * FROM pending_submissions ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingSubmissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(submission: PendingSubmissionEntity)

    @Delete
    suspend fun delete(submission: PendingSubmissionEntity)

    @Query("UPDATE pending_submissions SET retryCount = retryCount + 1 WHERE localId = :localId")
    suspend fun incrementRetry(localId: String)

    @Query("DELETE FROM pending_submissions WHERE retryCount >= 5")
    suspend fun deleteFailedSubmissions()
}

// ─── Bookmark DAO ─────────────────────────────────────────────────────────────
@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE itemType = :type ORDER BY createdAt DESC")
    fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE itemId = :itemId AND itemType = :type)")
    fun isBookmarked(itemId: String, type: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE itemId = :itemId AND itemType = :type")
    suspend fun delete(itemId: String, type: String)

    @Query("DELETE FROM bookmarks WHERE itemId IN (:itemIds)")
    suspend fun deleteOrphans(itemIds: List<String>)
}

// ─── Analytics DAO ────────────────────────────────────────────────────────────
@Dao
interface AnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLog(log: DailyReviewLogEntity)

    @Query("""
        UPDATE daily_review_logs
        SET reviewCount = reviewCount + :delta, archivedCount = archivedCount + :archived
        WHERE dayEpoch = :dayEpoch
    """)
    suspend fun incrementLog(dayEpoch: Long, delta: Int, archived: Int)

    @Query("SELECT * FROM daily_review_logs WHERE dayEpoch >= :fromDay ORDER BY dayEpoch ASC")
    suspend fun getLogsFrom(fromDay: Long): List<DailyReviewLogEntity>

    @Query("SELECT * FROM daily_review_logs ORDER BY dayEpoch DESC LIMIT 7")
    suspend fun getLast7Days(): List<DailyReviewLogEntity>
}
