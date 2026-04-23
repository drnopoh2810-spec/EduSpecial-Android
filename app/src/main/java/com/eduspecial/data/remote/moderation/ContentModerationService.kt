package com.eduspecial.data.remote.moderation

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentModerationService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    companion object {
        private const val TAG = "ContentModeration"
        private const val COLLECTION_MODERATION_RULES = "moderation_rules"
        private const val COLLECTION_FLAGGED_CONTENT = "flagged_content"
        private const val COLLECTION_USER_REPORTS = "user_reports"
        private const val COLLECTION_BLACKLIST = "content_blacklist"
    }
    
    /**
     * Moderates content before it's published.
     * Returns ModerationResult with decision and confidence score.
     */
    suspend fun moderateContent(
        content: String,
        contentType: ContentType,
        authorId: String,
        additionalContext: Map<String, Any> = emptyMap()
    ): ModerationResult {
        return try {
            Log.d(TAG, "🔍 Moderating ${contentType.name} content from user: $authorId")
            
            // Step 1: Quick blacklist check
            val blacklistResult = checkBlacklist(content)
            if (blacklistResult.isBlocked) {
                return ModerationResult(
                    decision = ModerationDecision.REJECT,
                    confidence = 1.0f,
                    reason = "Blacklisted content: ${blacklistResult.matchedTerm}",
                    flags = listOf(ModerationFlag.BLACKLISTED_CONTENT)
                )
            }
            
            // Step 2: Content analysis
            val analysisResult = analyzeContent(content, contentType)
            
            // Step 3: User reputation check
            val userScore = getUserReputationScore(authorId)
            
            // Step 4: Make final decision
            val finalDecision = makeModerationDecision(
                analysisResult, userScore, contentType, additionalContext
            )
            
            // Step 5: Log moderation action
            logModerationAction(content, contentType, authorId, finalDecision)
            
            Log.d(TAG, "✅ Moderation complete: ${finalDecision.decision} (${finalDecision.confidence})")
            finalDecision
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Moderation failed: ${e.message}")
            // Fail-safe: allow content but flag for manual review
            ModerationResult(
                decision = ModerationDecision.APPROVE_WITH_REVIEW,
                confidence = 0.0f,
                reason = "Moderation system error: ${e.message}",
                flags = listOf(ModerationFlag.SYSTEM_ERROR)
            )
        }
    }
    
    /**
     * Checks content against blacklisted terms and patterns.
     */
    private suspend fun checkBlacklist(content: String): BlacklistResult {
        return try {
            val blacklistDoc = firestore.collection(COLLECTION_BLACKLIST)
                .document("terms")
                .get()
                .await()
            
            if (!blacklistDoc.exists()) {
                return BlacklistResult(false, null)
            }
            
            val blacklistedTerms = blacklistDoc.get("terms") as? List<String> ?: emptyList()
            val patterns = blacklistDoc.get("patterns") as? List<String> ?: emptyList()
            
            val contentLower = content.lowercase()
            
            // Check exact terms
            for (term in blacklistedTerms) {
                if (contentLower.contains(term.lowercase())) {
                    return BlacklistResult(true, term)
                }
            }
            
            // Check regex patterns
            for (pattern in patterns) {
                try {
                    if (Regex(pattern, RegexOption.IGNORE_CASE).containsMatchIn(content)) {
                        return BlacklistResult(true, "Pattern: $pattern")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Invalid regex pattern: $pattern")
                }
            }
            
            BlacklistResult(false, null)
        } catch (e: Exception) {
            Log.e(TAG, "Blacklist check failed: ${e.message}")
            BlacklistResult(false, null)
        }
    }
    
    /**
     * Analyzes content using various heuristics and rules.
     */
    private suspend fun analyzeContent(
        content: String, 
        contentType: ContentType
    ): ContentAnalysisResult {
        val flags = mutableListOf<ModerationFlag>()
        var riskScore = 0.0f
        
        // Length checks
        when (contentType) {
            ContentType.FLASHCARD_TERM -> {
                if (content.length > 200) flags.add(ModerationFlag.EXCESSIVE_LENGTH)
                if (content.length < 2) flags.add(ModerationFlag.TOO_SHORT)
            }
            ContentType.FLASHCARD_DEFINITION -> {
                if (content.length > 1000) flags.add(ModerationFlag.EXCESSIVE_LENGTH)
                if (content.length < 5) flags.add(ModerationFlag.TOO_SHORT)
            }
            ContentType.QUESTION -> {
                if (content.length > 500) flags.add(ModerationFlag.EXCESSIVE_LENGTH)
                if (content.length < 10) flags.add(ModerationFlag.TOO_SHORT)
            }
            ContentType.ANSWER -> {
                if (content.length > 2000) flags.add(ModerationFlag.EXCESSIVE_LENGTH)
                if (content.length < 5) flags.add(ModerationFlag.TOO_SHORT)
            }
            ContentType.USER_PROFILE -> {
                if (content.length > 100) flags.add(ModerationFlag.EXCESSIVE_LENGTH)
                if (content.length < 2) flags.add(ModerationFlag.TOO_SHORT)
            }
        }
        
        // Content quality checks
        val upperCaseRatio = content.count { it.isUpperCase() }.toFloat() / content.length
        if (upperCaseRatio > 0.7f) {
            flags.add(ModerationFlag.EXCESSIVE_CAPS)
            riskScore += 0.3f
        }
        
        // Repetitive content check
        if (hasRepetitiveContent(content)) {
            flags.add(ModerationFlag.REPETITIVE_CONTENT)
            riskScore += 0.4f
        }
        
        // Spam indicators
        if (hasSpamIndicators(content)) {
            flags.add(ModerationFlag.SPAM_INDICATORS)
            riskScore += 0.5f
        }
        
        // Educational relevance (basic check)
        if (!isEducationallyRelevant(content, contentType)) {
            flags.add(ModerationFlag.OFF_TOPIC)
            riskScore += 0.3f
        }
        
        return ContentAnalysisResult(
            riskScore = riskScore.coerceAtMost(1.0f),
            flags = flags,
            confidence = if (flags.isEmpty()) 0.9f else 0.7f
        )
    }
    
    /**
     * Gets user reputation score based on their history.
     */
    private suspend fun getUserReputationScore(userId: String): Float {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) return 0.5f // Neutral for new users
            
            val contributionCount = (userDoc.getLong("contributionCount") ?: 0).toInt()
            val points = (userDoc.getLong("points") ?: 0).toInt()
            val joinedAt = userDoc.getLong("createdAt") ?: System.currentTimeMillis()
            
            // Calculate reputation based on contributions, points, and account age
            val daysSinceJoined = (System.currentTimeMillis() - joinedAt) / (24 * 60 * 60 * 1000)
            val accountAgeScore = (daysSinceJoined / 30.0f).coerceAtMost(1.0f) // Max 1.0 after 30 days
            val contributionScore = (contributionCount / 50.0f).coerceAtMost(1.0f) // Max 1.0 after 50 contributions
            val pointsScore = (points / 1000.0f).coerceAtMost(1.0f) // Max 1.0 after 1000 points
            
            ((accountAgeScore + contributionScore + pointsScore) / 3.0f).coerceIn(0.1f, 1.0f)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user reputation: ${e.message}")
            0.5f // Default neutral score
        }
    }
    
    /**
     * Makes final moderation decision based on all factors.
     */
    private fun makeModerationDecision(
        analysisResult: ContentAnalysisResult,
        userScore: Float,
        contentType: ContentType,
        context: Map<String, Any>
    ): ModerationResult {
        val riskScore = analysisResult.riskScore
        val flags = analysisResult.flags
        
        // Adjust risk based on user reputation
        val adjustedRisk = riskScore * (2.0f - userScore) // Lower risk for trusted users
        
        val decision = when {
            // High risk content - reject
            adjustedRisk > 0.8f || flags.contains(ModerationFlag.BLACKLISTED_CONTENT) -> {
                ModerationDecision.REJECT
            }
            // Medium risk - approve with review
            adjustedRisk > 0.5f || flags.any { it.requiresReview() } -> {
                ModerationDecision.APPROVE_WITH_REVIEW
            }
            // Low risk - approve
            else -> ModerationDecision.APPROVE
        }
        
        return ModerationResult(
            decision = decision,
            confidence = analysisResult.confidence * userScore,
            reason = generateModerationReason(flags, adjustedRisk, userScore),
            flags = flags,
            riskScore = adjustedRisk,
            userReputationScore = userScore
        )
    }
    
    /**
     * Logs moderation action for analytics and audit.
     */
    private suspend fun logModerationAction(
        content: String,
        contentType: ContentType,
        authorId: String,
        result: ModerationResult
    ) {
        try {
            firestore.collection("moderation_logs").add(mapOf(
                "contentType" to contentType.name,
                "authorId" to authorId,
                "contentLength" to content.length,
                "decision" to result.decision.name,
                "confidence" to result.confidence,
                "riskScore" to result.riskScore,
                "flags" to result.flags.map { it.name },
                "reason" to result.reason,
                "timestamp" to System.currentTimeMillis()
            ))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to log moderation action: ${e.message}")
        }
    }
    
    /**
     * Reports content by users for manual review.
     */
    suspend fun reportContent(
        contentId: String,
        contentType: ContentType,
        reporterId: String,
        reason: ReportReason,
        additionalInfo: String = ""
    ): Boolean {
        return try {
            firestore.collection(COLLECTION_USER_REPORTS).add(mapOf(
                "contentId" to contentId,
                "contentType" to contentType.name,
                "reporterId" to reporterId,
                "reason" to reason.name,
                "additionalInfo" to additionalInfo,
                "status" to "PENDING",
                "reportedAt" to System.currentTimeMillis()
            )).await()
            
            Log.d(TAG, "✅ Content reported: $contentId by $reporterId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to report content: ${e.message}")
            false
        }
    }
    
    // Helper methods
    private fun hasRepetitiveContent(content: String): Boolean {
        val words = content.split("\\s+".toRegex())
        if (words.size < 5) return false
        
        val wordCounts = words.groupingBy { it.lowercase() }.eachCount()
        val maxCount = wordCounts.values.maxOrNull() ?: 0
        return maxCount > words.size * 0.3 // More than 30% repetition
    }
    
    private fun hasSpamIndicators(content: String): Boolean {
        val spamPatterns = listOf(
            "اضغط هنا", "رابط", "تحميل", "مجاني", "عرض خاص",
            "click here", "download", "free", "special offer"
        )
        val contentLower = content.lowercase()
        return spamPatterns.any { contentLower.contains(it) }
    }
    
    private fun isEducationallyRelevant(content: String, contentType: ContentType): Boolean {
        // Basic educational relevance check
        val educationalKeywords = listOf(
            "تعلم", "تعليم", "درس", "شرح", "مفهوم", "تعريف", "مثال",
            "learn", "education", "lesson", "explain", "concept", "definition", "example",
            "علاج", "سلوك", "نطق", "تطوير", "مهارة", "تدريب",
            "therapy", "behavior", "speech", "development", "skill", "training"
        )
        
        val contentLower = content.lowercase()
        return educationalKeywords.any { contentLower.contains(it) } || content.length > 50
    }
    
    private fun generateModerationReason(
        flags: List<ModerationFlag>,
        riskScore: Float,
        userScore: Float
    ): String {
        return when {
            flags.contains(ModerationFlag.BLACKLISTED_CONTENT) -> "محتوى محظور"
            flags.contains(ModerationFlag.SPAM_INDICATORS) -> "مؤشرات سبام"
            flags.contains(ModerationFlag.OFF_TOPIC) -> "محتوى غير تعليمي"
            flags.contains(ModerationFlag.EXCESSIVE_CAPS) -> "استخدام مفرط للأحرف الكبيرة"
            flags.contains(ModerationFlag.REPETITIVE_CONTENT) -> "محتوى متكرر"
            riskScore > 0.5f -> "محتوى عالي المخاطر (${(riskScore * 100).toInt()}%)"
            userScore < 0.3f -> "مستخدم جديد - مراجعة احترازية"
            else -> "تمت الموافقة"
        }
    }
}

// Data classes and enums
data class ModerationResult(
    val decision: ModerationDecision,
    val confidence: Float,
    val reason: String,
    val flags: List<ModerationFlag> = emptyList(),
    val riskScore: Float = 0.0f,
    val userReputationScore: Float = 0.5f
)

data class ContentAnalysisResult(
    val riskScore: Float,
    val flags: List<ModerationFlag>,
    val confidence: Float
)

data class BlacklistResult(
    val isBlocked: Boolean,
    val matchedTerm: String?
)

enum class ModerationDecision {
    APPROVE,           // Content is safe, publish immediately
    APPROVE_WITH_REVIEW, // Content is probably safe, publish but flag for review
    REJECT             // Content is unsafe, don't publish
}

enum class ContentType {
    FLASHCARD_TERM,
    FLASHCARD_DEFINITION,
    QUESTION,
    ANSWER,
    USER_PROFILE
}

enum class ModerationFlag {
    BLACKLISTED_CONTENT,
    SPAM_INDICATORS,
    OFF_TOPIC,
    EXCESSIVE_CAPS,
    REPETITIVE_CONTENT,
    EXCESSIVE_LENGTH,
    TOO_SHORT,
    SYSTEM_ERROR;
    
    fun requiresReview(): Boolean = when (this) {
        BLACKLISTED_CONTENT, SPAM_INDICATORS, OFF_TOPIC -> true
        else -> false
    }
}

enum class ReportReason {
    INAPPROPRIATE_CONTENT,
    SPAM,
    OFF_TOPIC,
    HARASSMENT,
    COPYRIGHT_VIOLATION,
    MISINFORMATION,
    OTHER
}