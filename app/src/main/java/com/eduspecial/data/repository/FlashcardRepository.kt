package com.eduspecial.data.repository

import android.util.Log
import com.eduspecial.data.local.dao.FlashcardDao
import com.eduspecial.data.local.dao.PendingSubmissionDao
import com.eduspecial.data.local.entities.FlashcardEntity
import com.eduspecial.data.local.entities.PendingSubmissionEntity
import com.eduspecial.domain.model.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepository @Inject constructor(
    private val flashcardDao: FlashcardDao,
    private val pendingDao: PendingSubmissionDao,
    private val firestore: FirebaseFirestore,
    private val leaderboardRepository: com.eduspecial.data.repository.LeaderboardRepository,
    private val algoliaSearchService: com.eduspecial.data.remote.search.AlgoliaSearchService,
    private val moderationRepository: com.eduspecial.data.repository.ModerationRepository
) {
    private val col = firestore.collection("flashcards")
    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var listenerRegistration: ListenerRegistration? = null

    init {
        startRealtimeListener()
    }

    fun startRealtimeListener() {
        listenerRegistration?.remove()
        listenerRegistration = col
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FlashcardRepo", "Realtime listener error", error)
                    return@addSnapshotListener
                }
                snapshot ?: return@addSnapshotListener

                repoScope.launch {
                    for (change in snapshot.documentChanges) {
                        val doc = change.document
                        when (change.type) {
                            DocumentChange.Type.ADDED,
                            DocumentChange.Type.MODIFIED -> {
                                val entity = doc.toFlashcardEntity() ?: continue
                                val existing = flashcardDao.getFlashcardById(entity.id)
                                if (existing != null && existing.isPendingSync) continue
                                val merged = if (existing != null) {
                                    entity.copy(
                                        reviewState  = existing.reviewState,
                                        easeFactor   = existing.easeFactor,
                                        interval     = existing.interval,
                                        nextReviewDate = existing.nextReviewDate
                                    )
                                } else entity
                                flashcardDao.insert(merged)
                            }
                            DocumentChange.Type.REMOVED -> {
                                val existing = flashcardDao.getFlashcardById(doc.id)
                                existing?.let { flashcardDao.delete(it) }
                            }
                        }
                    }
                }
            }
    }

    fun stopRealtimeListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    // ─── Read (Room — offline-first) ──────────────────────────────────────────

    fun getAllFlashcards(): Flow<List<Flashcard>> =
        flashcardDao.getAllFlashcards().map { it.map { e -> e.toDomain() } }

    fun getStudyQueue(): Flow<List<Flashcard>> =
        flashcardDao.getStudyQueue().map { it.map { e -> e.toDomain() } }

    fun getArchivedFlashcards(): Flow<List<Flashcard>> =
        flashcardDao.getArchivedFlashcards().map { it.map { e -> e.toDomain() } }

    fun getByCategory(category: FlashcardCategory): Flow<List<Flashcard>> =
        flashcardDao.getByCategory(category.name).map { it.map { e -> e.toDomain() } }

    suspend fun searchLocal(query: String): List<Flashcard> =
        flashcardDao.searchFlashcards(query).map { it.toDomain() }

    suspend fun search(
        query: String, 
        category: FlashcardCategory? = null,
        useAlgolia: Boolean = true
    ): List<Flashcard> {
        return if (useAlgolia && algoliaSearchService.isAvailable()) {
            val result = algoliaSearchService.searchFlashcards(query, category)
            result.getOrElse { 
                Log.w("FlashcardRepo", "Algolia search failed, falling back to local")
                searchLocal(query)
            }
        } else {
            searchLocal(query)
        }
    }

    suspend fun getSearchSuggestions(query: String): List<String> {
        return if (algoliaSearchService.isAvailable()) {
            algoliaSearchService.getSuggestions(query)
        } else {
            emptyList()
        }
    }

    // ─── Duplicate Check ──────────────────────────────────────────────────────

    suspend fun checkDuplicate(term: String): DuplicateCheckResult {
        if (flashcardDao.countByTerm(term.trim()) > 0)
            return DuplicateCheckResult.IsDuplicate(emptyList())
        return try {
            val snap = col.whereEqualTo("term", term.trim()).limit(1).get().await()
            if (!snap.isEmpty) DuplicateCheckResult.IsDuplicate(emptyList())
            else DuplicateCheckResult.NotDuplicate
        } catch (e: Exception) {
            DuplicateCheckResult.NotDuplicate 
        }
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    suspend fun createFlashcard(
        term: String,
        definition: String,
        category: FlashcardCategory,
        mediaUrl: String?,
        mediaType: MediaType,
        contributorId: String
    ): Result<Flashcard> {
        val moderationResult = moderationRepository.moderateFlashcard(
            term = term,
            definition = definition,
            authorId = contributorId
        )
        
        if (moderationResult.decision == com.eduspecial.data.remote.moderation.ModerationDecision.REJECT) {
            return Result.failure(Exception("المحتوى مرفوض: ${moderationResult.termResult.reason}"))
        }
        
        val id  = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val entity = FlashcardEntity(
            id           = id,
            term         = term.trim(),
            definition   = definition.trim(),
            category     = category.name,
            mediaUrl     = mediaUrl,
            mediaType    = mediaType.name,
            contributor  = contributorId,
            createdAt    = now,
            isPendingSync = true
        )

        flashcardDao.insert(entity)

        return try {
            col.document(id).set(
                mapOf(
                    "id"          to id,
                    "term"        to term.trim(),
                    "definition"  to definition.trim(),
                    "category"    to category.name,
                    "mediaUrl"    to mediaUrl,
                    "mediaType"   to mediaType.name,
                    "contributor" to contributorId,
                    "createdAt"   to now,
                    "reviewState" to "NEW",
                    "upvotes"     to 0,
                    "moderationStatus" to moderationResult.decision.name,
                    "moderationFlags" to moderationResult.combinedFlags.map { it.name }
                )
            ).await()
            
            flashcardDao.insert(entity.copy(isPendingSync = false))
            
            if (moderationResult.requiresReview) {
                moderationRepository.addToPendingReview(
                    contentId = id,
                    contentType = com.eduspecial.data.remote.moderation.ContentType.FLASHCARD_TERM,
                    content = "$term | $definition",
                    authorId = contributorId,
                    moderationResult = moderationResult.termResult
                )
            }
            
            leaderboardRepository.awardFlashcardPoints(contributorId)
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Log.d("FlashcardRepo", "Offline create — queued for sync: $id")
            pendingDao.insert(PendingSubmissionEntity(
                localId = UUID.randomUUID().toString(),
                type    = PendingSubmissionEntity.TYPE_FLASHCARD,
                payload = """{"term":"$term","definition":"$definition","category":"${category.name}","mediaUrl":${if (mediaUrl != null) "\"$mediaUrl\"" else "null"},"mediaType":"${mediaType.name}","contributorId":"$contributorId"}"""
            ))
            Result.success(entity.toDomain())
        }
    }

    // ─── SRS Review ───────────────────────────────────────────────────────────

    suspend fun processReview(flashcard: Flashcard, result: SRSResult) {
        val (newState, newEase, newInterval) = calculateNextSRS(flashcard, result)
        val nextReview = System.currentTimeMillis() + (newInterval * 86_400_000L)
        flashcardDao.updateReviewState(flashcard.id, newState.name, newEase, newInterval, nextReview)
        try {
            col.document(flashcard.id).update(
                mapOf(
                    "reviewState"    to newState.name,
                    "easeFactor"     to newEase,
                    "interval"       to newInterval,
                    "nextReviewDate" to nextReview
                )
            ).await()
        } catch (_: Exception) { }
    }

    // ─── Refresh (initial load / pull-to-refresh) ─────────────────────────────

    suspend fun refreshFromServer(page: Int = 1): Result<Unit> {
        return try {
            var lastDoc: com.google.firebase.firestore.DocumentSnapshot? = null
            var hasMore = true
            var totalFetched = 0

            while (hasMore) {
                val query = col
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .let { q -> if (lastDoc != null) q.startAfter(lastDoc!!) else q }
                    .limit(100)

                val snap = query.get().await()
                val entities = snap.documents.mapNotNull { it.toFlashcardEntity() }

                if (entities.isNotEmpty()) {
                    flashcardDao.insertAll(entities)
                    totalFetched += entities.size
                    lastDoc = snap.documents.lastOrNull()
                }
                hasMore = snap.documents.size == 100
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Incremental Sync ─────────────────────────────────────────────────────

    suspend fun syncFromServer(since: Long): Result<Unit> {
        return try {
            val snap = col
                .whereGreaterThan("createdAt", since)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get().await()
            val entities = snap.documents.mapNotNull { it.toFlashcardEntity() }
            if (entities.isNotEmpty()) flashcardDao.insertAll(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            refreshFromServer()
        }
    }

    // ─── Edit ─────────────────────────────────────────────────────────────────

    suspend fun editFlashcard(
        id: String, term: String, definition: String,
        category: FlashcardCategory, mediaUrl: String?, mediaType: MediaType
    ): Result<Flashcard> {
        flashcardDao.updateContent(id, term, definition, category.name, mediaUrl, mediaType.name)
        return try {
            col.document(id).update(
                mapOf(
                    "term"       to term,
                    "definition" to definition,
                    "category"   to category.name,
                    "mediaUrl"    to mediaUrl,
                    "mediaType"  to mediaType.name
                )
            ).await()
            val entity = flashcardDao.getFlashcardById(id)
            Result.success(entity?.toDomain() ?: Flashcard(
                id = id, term = term, definition = definition,
                category = category, mediaUrl = mediaUrl, mediaType = mediaType, contributor = ""
            ))
        } catch (e: Exception) {
            pendingDao.insert(PendingSubmissionEntity(
                localId = UUID.randomUUID().toString(),
                type    = PendingSubmissionEntity.TYPE_FLASHCARD_EDIT,
                payload = """{"id":"$id","term":"$term","definition":"$definition","category":"${category.name}","mediaUrl":${if (mediaUrl != null) "\"$mediaUrl\"" else "null"},"mediaType":"${mediaType.name}"}"""
            ))
            val entity = flashcardDao.getFlashcardById(id)
            Result.success(entity?.toDomain() ?: Flashcard(
                id = id, term = term, definition = definition,
                category = category, mediaUrl = mediaUrl, mediaType = mediaType, contributor = ""
            ))
        }
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    suspend fun deleteFlashcard(id: String): Result<Unit> {
        return try {
            val entity = flashcardDao.getFlashcardById(id)
            entity?.let { flashcardDao.delete(it) }
            try { col.document(id).delete().await() } catch (_: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Category Mastery ─────────────────────────────────────────────────────

    suspend fun getCategoryMastery(): List<CategoryMastery> =
        flashcardDao.getCategoryMastery().map { row ->
            CategoryMastery(
                category = try { FlashcardCategory.valueOf(row.category) }
                           catch (_: Exception) { FlashcardCategory.ABA_THERAPY },
                total    = row.total,
                archived = row.archived
            )
        }

    suspend fun getDueCount(): Int = flashcardDao.getDueCount()

    // ─── SM2 Algorithm ────────────────────────────────────────────────────────

    private fun calculateNextSRS(
        card: Flashcard, result: SRSResult
    ): Triple<ReviewState, Float, Int> = when (result) {
        is SRSResult.Easy  -> Triple(ReviewState.ARCHIVED, minOf(card.easeFactor + 0.15f, 2.5f), card.interval * 4)
        is SRSResult.Good  -> Triple(ReviewState.REVIEW,   card.easeFactor, (card.interval * card.easeFactor).toInt().coerceAtLeast(1))
        is SRSResult.Hard  -> Triple(ReviewState.REVIEW,   maxOf(card.easeFactor - 0.15f, 1.3f), (card.interval * 1.2f).toInt().coerceAtLeast(1))
        is SRSResult.Again -> Triple(ReviewState.LEARNING, maxOf(card.easeFactor - 0.2f,  1.3f), 1)
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toFlashcardEntity(): FlashcardEntity? {
    return try {
        FlashcardEntity(
            id           = id,
            term         = getString("term")        ?: return null,
            definition   = getString("definition")  ?: return null,
            category     = getString("category")    ?: "ABA_THERAPY",
            mediaUrl     = getString("mediaUrl"),
            mediaType    = getString("mediaType")   ?: "NONE",
            contributor  = getString("contributor") ?: "",
            createdAt    = getLong("createdAt")     ?: System.currentTimeMillis(),
            reviewState  = getString("reviewState") ?: "NEW",
            isPendingSync = false
        )
    } catch (_: Exception) { null }
}

sealed class DuplicateCheckResult {
    data object NotDuplicate : DuplicateCheckResult()
    data class IsDuplicate(val similarTerms: List<String>) : DuplicateCheckResult()
}

fun FlashcardEntity.toDomain() = Flashcard(
    id             = id,
    term           = term,
    definition     = definition,
    category       = FlashcardCategory.valueOf(category),
    mediaUrl       = mediaUrl,
    mediaType      = MediaType.valueOf(mediaType),
    contributor    = contributor,
    createdAt      = Date(createdAt),
    reviewState    = ReviewState.valueOf(reviewState),
    easeFactor     = easeFactor,
    interval       = interval,
    nextReviewDate = Date(nextReviewDate),
    isOfflineCached = isOfflineCached
)

fun com.eduspecial.data.remote.dto.FlashcardDto.toDomain() = Flashcard(
    id          = id,
    term        = term,
    definition  = definition,
    category    = try { FlashcardCategory.valueOf(category) } catch (_: Exception) { FlashcardCategory.ABA_THERAPY },
    mediaUrl    = mediaUrl,
    mediaType   = try { MediaType.valueOf(mediaType) } catch (_: Exception) { MediaType.NONE },
    contributor = contributor,
    createdAt   = Date(createdAt)
)

fun com.eduspecial.data.remote.dto.FlashcardDto.toEntity() = FlashcardEntity(
    id           = id,
    term         = term,
    definition   = definition,
    category     = category,
    mediaUrl     = mediaUrl,
    mediaType    = mediaType,
    contributor  = contributor,
    createdAt    = createdAt,
    isPendingSync = false
)
