package com.eduspecial.data.repository

import android.util.Log
import com.eduspecial.data.remote.moderation.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModerationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val contentModerationService: ContentModerationService
) {
    
    companion object {
        private const val TAG = "ModerationRepository"
        private const val COLLECTION_PENDING_REVIEW = "pending_review"
        private const val COLLECTION_MODERATION_ACTIONS = "moderation_actions"
    }
    
    /**
     * Moderates flashcard content before creation.
     */
    suspend fun moderateFlashcard(
        term: String,
        definition: String,
        authorId: String
    ): FlashcardModerationResult {
        return try {
            // Moderate term
            val termResult = contentModerationService.moderateContent(
                content = term,
                contentType = ContentType.FLASHCARD_TERM,
                authorId = authorId
            )
            
            // Moderate definition
            val definitionResult = contentModerationService.moderateContent(
                content = definition,
                contentType = ContentType.FLASHCARD_DEFINITION,
                authorId = authorId
            )
            
            // Combine results
            val overallDecision = when {
                termResult.decision == ModerationDecision.REJECT || 
                definitionResult.decision == ModerationDecision.REJECT -> ModerationDecision.REJECT
                
                termResult.decision == ModerationDecision.APPROVE_WITH_REVIEW || 
                definitionResult.decision == ModerationDecision.APPROVE_WITH_REVIEW -> ModerationDecision.APPROVE_WITH_REVIEW
                
                else -> ModerationDecision.APPROVE
            }
            
            val combinedFlags = (termResult.flags + definitionResult.flags).distinct()
            val averageConfidence = (termResult.confidence + definitionResult.confidence) / 2
            
            FlashcardModerationResult(
                decision = overallDecision,
                termResult = termResult,
                definitionResult = definitionResult,
                overallConfidence = averageConfidence,
                combinedFlags = combinedFlags,
                requiresReview = overallDecision == ModerationDecision.APPROVE_WITH_REVIEW
            )
        } catch (e: Exception) {
            Log.e(TAG, "Flashcard moderation failed: ${e.message}")
            FlashcardModerationResult(
                decision = ModerationDecision.APPROVE_WITH_REVIEW,
                termResult = ModerationResult(ModerationDecision.APPROVE_WITH_REVIEW, 0.0f, "Error"),
                definitionResult = ModerationResult(ModerationDecision.APPROVE_WITH_REVIEW, 0.0f, "Error"),
                overallConfidence = 0.0f,
                combinedFlags = listOf(ModerationFlag.SYSTEM_ERROR),
                requiresReview = true
            )
        }
    }
    
    /**
     * Moderates question content before posting.
     */
    suspend fun moderateQuestion(
        question: String,
        authorId: String
    ): ModerationResult {
        return contentModerationService.moderateContent(
            content = question,
            contentType = ContentType.QUESTION,
            authorId = authorId
        )
    }
    
    /**
     * Moderates answer content before posting.
     */
    suspend fun moderateAnswer(
        answer: String,
        authorId: String,
        questionId: String
    ): ModerationResult {
        return contentModerationService.moderateContent(
            content = answer,
            contentType = ContentType.ANSWER,
            authorId = authorId,
            additionalContext = mapOf("questionId" to questionId)
        )
    }
    
    /**
     * Reports content for manual review.
     */
    suspend fun reportContent(
        contentId: String,
        contentType: ContentType,
        reason: ReportReason,
        additionalInfo: String = ""
    ): Boolean {
        val reporterId = auth.currentUser?.uid ?: return false
        
        return contentModerationService.reportContent(
            contentId = contentId,
            contentType = contentType,
            reporterId = reporterId,
            reason = reason,
            additionalInfo = additionalInfo
        )
    }
    
    /**
     * Adds content to pending review queue.
     */
    suspend fun addToPendingReview(
        contentId: String,
        contentType: ContentType,
        content: String,
        authorId: String,
        moderationResult: ModerationResult
    ): Boolean {
        return try {
            firestore.collection(COLLECTION_PENDING_REVIEW).add(mapOf(
                "contentId" to contentId,
                "contentType" to contentType.name,
                "content" to content,
                "authorId" to authorId,
                "moderationResult" to mapOf(
                    "decision" to moderationResult.decision.name,
                    "confidence" to moderationResult.confidence,
                    "reason" to moderationResult.reason,
                    "flags" to moderationResult.flags.map { it.name },
                    "riskScore" to moderationResult.riskScore
                ),
                "status" to "PENDING",
                "priority" to calculateReviewPriority(moderationResult),
                "createdAt" to System.currentTimeMillis(),
                "reviewedAt" to null,
                "reviewerId" to null,
                "reviewDecision" to null
            )).await()
            
            Log.d(TAG, "✅ Content added to pending review: $contentId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to add content to pending review: ${e.message}")
            false
        }
    }
    
    /**
     * Gets pending review items for moderators.
     */
    suspend fun getPendingReviewItems(limit: Int = 20): List<PendingReviewItem> {
        return try {
            val snapshot = firestore.collection(COLLECTION_PENDING_REVIEW)
                .whereEqualTo("status", "PENDING")
                .orderBy("priority", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                try {
                    PendingReviewItem(
                        id = doc.id,
                        contentId = doc.getString("contentId") ?: "",
                        contentType = ContentType.valueOf(doc.getString("contentType") ?: ""),
                        content = doc.getString("content") ?: "",
                        authorId = doc.getString("authorId") ?: "",
                        priority = (doc.getLong("priority") ?: 0).toInt(),
                        createdAt = doc.getLong("createdAt") ?: 0,
                        moderationReason = doc.get("moderationResult.reason") as? String ?: "",
                        flags = (doc.get("moderationResult.flags") as? List<String>)?.map { 
                            ModerationFlag.valueOf(it) 
                        } ?: emptyList()
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse pending review item: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get pending review items: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Reviews content and makes final decision.
     */
    suspend fun reviewContent(
        reviewItemId: String,
        decision: ReviewDecision,
        reviewerNotes: String = ""
    ): Boolean {
        val reviewerId = auth.currentUser?.uid ?: return false
        
        return try {
            // Update review item
            firestore.collection(COLLECTION_PENDING_REVIEW)
                .document(reviewItemId)
                .update(mapOf(
                    "status" to "REVIEWED",
                    "reviewDecision" to decision.name,
                    "reviewerId" to reviewerId,
                    "reviewerNotes" to reviewerNotes,
                    "reviewedAt" to System.currentTimeMillis()
                ))
                .await()
            
            // Log moderation action
            firestore.collection(COLLECTION_MODERATION_ACTIONS).add(mapOf(
                "reviewItemId" to reviewItemId,
                "reviewerId" to reviewerId,
                "decision" to decision.name,
                "notes" to reviewerNotes,
                "timestamp" to System.currentTimeMillis()
            )).await()
            
            Log.d(TAG, "✅ Content reviewed: $reviewItemId -> ${decision.name}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to review content: ${e.message}")
            false
        }
    }
    
    /**
     * Gets moderation statistics for analytics.
     */
    suspend fun getModerationStats(days: Int = 7): ModerationStats {
        return try {
            val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000)
            
            // Get moderation logs
            val logsSnapshot = firestore.collection("moderation_logs")
                .whereGreaterThan("timestamp", cutoffTime)
                .get()
                .await()
            
            val logs = logsSnapshot.documents
            val totalItems = logs.size
            val approvedCount = logs.count { 
                (it.getString("decision") ?: "") == "APPROVE" 
            }
            val rejectedCount = logs.count { 
                (it.getString("decision") ?: "") == "REJECT" 
            }
            val reviewCount = logs.count { 
                (it.getString("decision") ?: "") == "APPROVE_WITH_REVIEW" 
            }
            
            // Get pending review count
            val pendingSnapshot = firestore.collection(COLLECTION_PENDING_REVIEW)
                .whereEqualTo("status", "PENDING")
                .get()
                .await()
            
            ModerationStats(
                totalItemsModerated = totalItems,
                approvedCount = approvedCount,
                rejectedCount = rejectedCount,
                pendingReviewCount = reviewCount,
                currentPendingCount = pendingSnapshot.size(),
                averageConfidence = logs.mapNotNull { 
                    (it.getDouble("confidence") ?: 0.0).toFloat() 
                }.average().toFloat(),
                periodDays = days
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get moderation stats: ${e.message}")
            ModerationStats()
        }
    }
    
    /**
     * Updates blacklist terms and patterns.
     */
    suspend fun updateBlacklist(
        terms: List<String>,
        patterns: List<String>
    ): Boolean {
        return try {
            firestore.collection("content_blacklist")
                .document("terms")
                .set(mapOf(
                    "terms" to terms,
                    "patterns" to patterns,
                    "updatedAt" to System.currentTimeMillis(),
                    "updatedBy" to (auth.currentUser?.uid ?: "system")
                ))
                .await()
            
            Log.d(TAG, "✅ Blacklist updated: ${terms.size} terms, ${patterns.size} patterns")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update blacklist: ${e.message}")
            false
        }
    }
    
    private fun calculateReviewPriority(result: ModerationResult): Int {
        return when {
            result.flags.contains(ModerationFlag.BLACKLISTED_CONTENT) -> 10
            result.flags.contains(ModerationFlag.SPAM_INDICATORS) -> 8
            result.riskScore > 0.7f -> 7
            result.flags.contains(ModerationFlag.OFF_TOPIC) -> 5
            result.userReputationScore < 0.3f -> 4
            else -> 3
        }
    }
}

// Data classes
data class FlashcardModerationResult(
    val decision: ModerationDecision,
    val termResult: ModerationResult,
    val definitionResult: ModerationResult,
    val overallConfidence: Float,
    val combinedFlags: List<ModerationFlag>,
    val requiresReview: Boolean
)

data class PendingReviewItem(
    val id: String,
    val contentId: String,
    val contentType: ContentType,
    val content: String,
    val authorId: String,
    val priority: Int,
    val createdAt: Long,
    val moderationReason: String,
    val flags: List<ModerationFlag>
)

data class ModerationStats(
    val totalItemsModerated: Int = 0,
    val approvedCount: Int = 0,
    val rejectedCount: Int = 0,
    val pendingReviewCount: Int = 0,
    val currentPendingCount: Int = 0,
    val averageConfidence: Float = 0.0f,
    val periodDays: Int = 7
)

enum class ReviewDecision {
    APPROVE,           // Approve the content
    REJECT,            // Reject the content
    REQUEST_CHANGES    // Ask author to modify content
}