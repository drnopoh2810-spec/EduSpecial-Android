package com.eduspecial.data.repository

import android.util.Log
import com.eduspecial.data.local.dao.QADao
import com.eduspecial.data.local.entities.QAAnswerEntity
import com.eduspecial.data.local.entities.QAQuestionEntity
import com.eduspecial.domain.model.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QARepository @Inject constructor(
    private val qaDao: QADao,
    private val firestore: FirebaseFirestore,
    private val leaderboardRepository: com.eduspecial.data.repository.LeaderboardRepository,
    private val algoliaSearchService: com.eduspecial.data.remote.search.AlgoliaSearchService,
    private val moderationRepository: com.eduspecial.data.repository.ModerationRepository
) {
    private val qCol = firestore.collection("questions")
    private val aCol = firestore.collection("answers")

    // ─── Read (Room — offline-first) ──────────────────────────────────────────
    fun getAllQuestions(): Flow<List<QAQuestion>> =
        qaDao.getAllQuestions().map { it.map { e -> e.toDomain() } }

    fun getUnansweredQuestions(): Flow<List<QAQuestion>> =
        qaDao.getUnansweredQuestions().map { it.map { e -> e.toDomain() } }

    fun getAnswersForQuestion(questionId: String): Flow<List<QAAnswer>> =
        qaDao.getAnswersForQuestion(questionId).map { it.map { e -> e.toDomain() } }

    /** Local full-text search on questions — instant, works offline */
    suspend fun searchLocal(query: String): List<QAQuestion> =
        qaDao.searchQuestions(query).map { it.toDomain() }

    /** 
     * Enhanced search using Algolia for better results.
     * Falls back to local search if Algolia is unavailable.
     */
    suspend fun search(
        query: String,
        category: FlashcardCategory? = null,
        unansweredOnly: Boolean = false,
        useAlgolia: Boolean = true
    ): List<QAQuestion> {
        return if (useAlgolia && algoliaSearchService.isAvailable()) {
            val result = algoliaSearchService.searchQuestions(query, category, unansweredOnly)
            result.getOrElse {
                Log.w("QARepository", "Algolia search failed, falling back to local")
                searchLocal(query)
            }
        } else {
            searchLocal(query)
        }
    }

    // ─── Duplicate Check ──────────────────────────────────────────────────────
    suspend fun checkDuplicate(question: String): DuplicateCheckResult {
        if (qaDao.countByQuestion(question) > 0)
            return DuplicateCheckResult.IsDuplicate(emptyList())
        return try {
            val snap = qCol.whereEqualTo("question", question).limit(1).get().await()
            if (!snap.isEmpty) DuplicateCheckResult.IsDuplicate(emptyList())
            else DuplicateCheckResult.NotDuplicate
        } catch (_: Exception) { DuplicateCheckResult.NotDuplicate }
    }

    // ─── Create Question ──────────────────────────────────────────────────────
    suspend fun createQuestion(
        question: String, category: FlashcardCategory,
        contributorId: String, tags: List<String>
    ): Result<QAQuestion> {
        // Step 1: Content moderation
        val moderationResult = moderationRepository.moderateQuestion(
            question = question,
            authorId = contributorId
        )
        
        // Check if content should be rejected
        if (moderationResult.decision == com.eduspecial.data.remote.moderation.ModerationDecision.REJECT) {
            return Result.failure(Exception("السؤال مرفوض: ${moderationResult.reason}"))
        }
        
        val id = UUID.randomUUID().toString()
        val entity = QAQuestionEntity(
            id = id, question = question, category = category.name,
            contributor = contributorId, tags = tags.joinToString(","),
            isPendingSync = true
        )
        qaDao.insertQuestion(entity)
        return try {
            qCol.document(id).set(mapOf(
                "id" to id, "question" to question, "category" to category.name,
                "contributor" to contributorId, "tags" to tags,
                "upvotes" to 0, "isAnswered" to false,
                "createdAt" to System.currentTimeMillis(),
                "moderationStatus" to moderationResult.decision.name,
                "moderationFlags" to moderationResult.flags.map { it.name }
            )).await()
            qaDao.insertQuestion(entity.copy(isPendingSync = false))
            
            // Handle moderation result
            if (moderationResult.decision == com.eduspecial.data.remote.moderation.ModerationDecision.APPROVE_WITH_REVIEW) {
                moderationRepository.addToPendingReview(
                    contentId = id,
                    contentType = com.eduspecial.data.remote.moderation.ContentType.QUESTION,
                    content = question,
                    authorId = contributorId,
                    moderationResult = moderationResult
                )
                Log.d("QARepository", "📋 Question flagged for review: $id")
            }
            
            // Award points for posting a question
            leaderboardRepository.awardQuestionPoints(contributorId)
            Result.success(entity.toDomain())
        } catch (_: Exception) {
            Result.success(entity.toDomain())
        }
    }

    // ─── Create Answer ────────────────────────────────────────────────────────
    suspend fun createAnswer(
        questionId: String, content: String, contributorId: String
    ): Result<QAAnswer> {
        // Step 1: Content moderation
        val moderationResult = moderationRepository.moderateAnswer(
            answer = content,
            authorId = contributorId,
            questionId = questionId
        )
        
        // Check if content should be rejected
        if (moderationResult.decision == com.eduspecial.data.remote.moderation.ModerationDecision.REJECT) {
            return Result.failure(Exception("الإجابة مرفوضة: ${moderationResult.reason}"))
        }
        
        val id = UUID.randomUUID().toString()
        val entity = QAAnswerEntity(
            id = id, questionId = questionId, content = content,
            contributor = contributorId
        )
        return try {
            aCol.document(id).set(mapOf(
                "id" to id, "questionId" to questionId, "content" to content,
                "contributor" to contributorId, "upvotes" to 0,
                "isAccepted" to false, "createdAt" to System.currentTimeMillis(),
                "moderationStatus" to moderationResult.decision.name,
                "moderationFlags" to moderationResult.flags.map { it.name }
            )).await()
            qaDao.insertAnswer(entity)
            
            // Handle moderation result
            if (moderationResult.decision == com.eduspecial.data.remote.moderation.ModerationDecision.APPROVE_WITH_REVIEW) {
                moderationRepository.addToPendingReview(
                    contentId = id,
                    contentType = com.eduspecial.data.remote.moderation.ContentType.ANSWER,
                    content = content,
                    authorId = contributorId,
                    moderationResult = moderationResult
                )
                Log.d("QARepository", "📋 Answer flagged for review: $id")
            }
            
            // Award points for posting an answer
            leaderboardRepository.awardAnswerPoints(contributorId)
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Upvote Question ──────────────────────────────────────────────────────
    suspend fun upvoteQuestion(id: String) {
        qaDao.upvoteQuestion(id)
        try { qCol.document(id).update("upvotes", FieldValue.increment(1)).await() }
        catch (_: Exception) {}
    }

    // ─── Upvote Answer ────────────────────────────────────────────────────────
    suspend fun upvoteAnswer(answerId: String): Result<Unit> {
        qaDao.upvoteAnswer(answerId)
        return try {
            aCol.document(answerId).update("upvotes", FieldValue.increment(1)).await()
            Result.success(Unit)
        } catch (_: Exception) { Result.success(Unit) }
    }

    // ─── Accept Answer ────────────────────────────────────────────────────────
    suspend fun acceptAnswer(answerId: String, questionId: String): Result<Unit> {
        qaDao.acceptAnswer(answerId)
        qaDao.markQuestionAnswered(questionId)
        return try {
            aCol.document(answerId).update("isAccepted", true).await()
            qCol.document(questionId).update("isAnswered", true).await()
            // Award bonus points to the answer author
            val answerDoc = aCol.document(answerId).get().await()
            val answerAuthor = answerDoc.getString("contributor") ?: ""
            if (answerAuthor.isNotBlank()) {
                leaderboardRepository.awardAcceptedAnswerPoints(answerAuthor)
            }
            Result.success(Unit)
        } catch (_: Exception) { Result.success(Unit) }
    }

    // ─── Edit Question ────────────────────────────────────────────────────────
    suspend fun editQuestion(id: String, question: String, category: FlashcardCategory): Result<QAQuestion> {
        qaDao.updateQuestion(id, question, category.name)
        return try {
            qCol.document(id).update(mapOf("question" to question, "category" to category.name)).await()
            val entity = qaDao.getAllQuestions().first().firstOrNull { it.id == id }
            Result.success(entity?.toDomain() ?: QAQuestion(id = id, question = question, category = category, contributor = ""))
        } catch (_: Exception) {
            val entity = qaDao.getAllQuestions().first().firstOrNull { it.id == id }
            Result.success(entity?.toDomain() ?: QAQuestion(id = id, question = question, category = category, contributor = ""))
        }
    }

    // ─── Edit Answer ──────────────────────────────────────────────────────────
    suspend fun editAnswer(id: String, content: String): Result<QAAnswer> {
        qaDao.updateAnswer(id, content)
        return try {
            aCol.document(id).update("content", content).await()
            Result.success(QAAnswer(id = id, questionId = "", content = content, contributor = ""))
        } catch (_: Exception) {
            Result.success(QAAnswer(id = id, questionId = "", content = content, contributor = ""))
        }
    }

    // ─── Refresh from Firestore ───────────────────────────────────────────────
    suspend fun refreshFromServer() {
        try {
            val snap = qCol.orderBy("createdAt", Query.Direction.DESCENDING).limit(50).get().await()
            val questions = snap.documents.mapNotNull { doc ->
                try {
                    QAQuestionEntity(
                        id = doc.id,
                        question = doc.getString("question") ?: return@mapNotNull null,
                        category = doc.getString("category") ?: "ABA_THERAPY",
                        contributor = doc.getString("contributor") ?: "",
                        upvotes = (doc.getLong("upvotes") ?: 0).toInt(),
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        isAnswered = doc.getBoolean("isAnswered") ?: false,
                        tags = (doc.get("tags") as? List<*>)?.joinToString(",") ?: ""
                    )
                } catch (_: Exception) { null }
            }
            qaDao.insertQuestions(questions)

            // Fetch answers for each question
            questions.forEach { q ->
                try {
                    val aSnap = aCol.whereEqualTo("questionId", q.id).get().await()
                    val answers = aSnap.documents.mapNotNull { doc ->
                        try {
                            QAAnswerEntity(
                                id = doc.id,
                                questionId = doc.getString("questionId") ?: return@mapNotNull null,
                                content = doc.getString("content") ?: return@mapNotNull null,
                                contributor = doc.getString("contributor") ?: "",
                                upvotes = (doc.getLong("upvotes") ?: 0).toInt(),
                                isAccepted = doc.getBoolean("isAccepted") ?: false,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                        } catch (_: Exception) { null }
                    }
                    qaDao.insertAnswers(answers)
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    suspend fun syncFromServer(since: Long) {
        try {
            val snap = qCol.whereGreaterThan("createdAt", since)
                .orderBy("createdAt", Query.Direction.ASCENDING).get().await()
            if (!snap.isEmpty) refreshFromServer()
        } catch (_: Exception) { refreshFromServer() }
    }
}

// ─── Mappers ──────────────────────────────────────────────────────────────────
fun QAQuestionEntity.toDomain() = QAQuestion(
    id = id, question = question, answers = emptyList(),
    category = try { FlashcardCategory.valueOf(category) } catch (_: Exception) { FlashcardCategory.ABA_THERAPY },
    contributor = contributor, upvotes = upvotes,
    createdAt = Date(createdAt), isAnswered = isAnswered,
    tags = if (tags.isEmpty()) emptyList() else tags.split(",")
)

fun QAAnswerEntity.toDomain() = QAAnswer(
    id = id, questionId = questionId, content = content,
    contributor = contributor, upvotes = upvotes,
    isAccepted = isAccepted, createdAt = Date(createdAt)
)

fun com.eduspecial.data.remote.dto.QAQuestionDto.toDomain() = QAQuestion(
    id = id, question = question,
    answers = answers.map { it.toDomain() },
    category = try { FlashcardCategory.valueOf(category) } catch (_: Exception) { FlashcardCategory.ABA_THERAPY },
    contributor = contributor, upvotes = upvotes,
    createdAt = Date(createdAt), isAnswered = isAnswered, tags = tags
)

fun com.eduspecial.data.remote.dto.QAAnswerDto.toDomain() = QAAnswer(
    id = id, questionId = questionId, content = content,
    contributor = contributor, upvotes = upvotes,
    isAccepted = isAccepted, createdAt = Date(createdAt)
)

fun com.eduspecial.data.remote.dto.QAQuestionDto.toQuestionEntity() = QAQuestionEntity(
    id = id, question = question, category = category,
    contributor = contributor, upvotes = upvotes,
    createdAt = createdAt, isAnswered = isAnswered,
    tags = tags.joinToString(",")
)

fun com.eduspecial.data.remote.dto.QAAnswerDto.toEntity() = QAAnswerEntity(
    id = id, questionId = questionId, content = content,
    contributor = contributor, upvotes = upvotes,
    isAccepted = isAccepted, createdAt = createdAt
)
