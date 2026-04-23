package com.eduspecial.domain.model

import java.util.Date

// ─── Flashcard Domain Model ───────────────────────────────────────────────────
data class Flashcard(
    val id: String,
    val term: String,                  // English term (deduplicated globally)
    val definition: String,
    val category: FlashcardCategory,
    val mediaUrl: String? = null,
    val mediaType: MediaType = MediaType.NONE,
    val contributor: String,
    val createdAt: Date = Date(),
    val reviewState: ReviewState = ReviewState.NEW,
    val easeFactor: Float = 2.5f,      // SRS ease factor
    val interval: Int = 1,             // SRS interval in days
    val nextReviewDate: Date = Date(),
    val isOfflineCached: Boolean = false
)

enum class FlashcardCategory {
    ABA_THERAPY,
    AUTISM_SPECTRUM,
    SENSORY_PROCESSING,
    SPEECH_LANGUAGE,
    OCCUPATIONAL_THERAPY,
    BEHAVIORAL_INTERVENTION,
    INCLUSIVE_EDUCATION,
    DEVELOPMENTAL_DISABILITIES,
    ASSESSMENT_TOOLS,
    FAMILY_SUPPORT
}

enum class MediaType { NONE, IMAGE, VIDEO, AUDIO }

enum class ReviewState {
    NEW,       // Not yet seen
    LEARNING,  // In learning queue
    REVIEW,    // Scheduled for review
    ARCHIVED   // Marked as "Easy" / mastered
}

// ─── Q&A Domain Model ────────────────────────────────────────────────────────
data class QAQuestion(
    val id: String,
    val question: String,
    val answers: List<QAAnswer> = emptyList(),
    val category: FlashcardCategory,
    val contributor: String,
    val upvotes: Int = 0,
    val createdAt: Date = Date(),
    val isAnswered: Boolean = false,
    val tags: List<String> = emptyList()
)

data class QAAnswer(
    val id: String,
    val questionId: String,
    val content: String,
    val contributor: String,
    val upvotes: Int = 0,
    val isAccepted: Boolean = false,
    val createdAt: Date = Date()
)

// ─── User Model ───────────────────────────────────────────────────────────────
data class User(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
    val contributionCount: Int = 0,
    val joinedAt: Date = Date()
)

// ─── SRS Review Result ────────────────────────────────────────────────────────
sealed class SRSResult {
    data object Easy : SRSResult()    // Archive the card
    data object Good : SRSResult()    // Normal review
    data object Hard : SRSResult()    // Shorter interval
    data object Again : SRSResult()   // Reset interval
}

// ─── Search Result ────────────────────────────────────────────────────────────
data class SearchResult(
    val id: String,
    val type: SearchResultType,
    val title: String,
    val subtitle: String,
    val highlightedText: String? = null
)

enum class SearchResultType { FLASHCARD, QUESTION }

// ─── Bookmark Models ──────────────────────────────────────────────────────────
enum class BookmarkType { FLASHCARD, QUESTION }

data class BookmarkCollection(
    val flashcards: List<Flashcard>,
    val questions: List<QAQuestion>
)

// ─── Analytics Models ─────────────────────────────────────────────────────────
data class DailyProgress(
    val dayEpoch: Long,
    val reviewCount: Int
)

data class CategoryMastery(
    val category: FlashcardCategory,
    val total: Int,
    val archived: Int
) {
    val percentage: Float get() = if (total == 0) 0f else archived.toFloat() / total
}

// ─── Leaderboard Models ───────────────────────────────────────────────────────

/**
 * Represents a single user entry in the leaderboard.
 *
 * Points system:
 *  +10  per accepted flashcard added
 *  +5   per question posted
 *  +3   per answer posted
 *  +15  per answer accepted (marked as best answer)
 */
data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val points: Int = 0,
    val flashcardsAdded: Int = 0,
    val questionsAsked: Int = 0,
    val answersGiven: Int = 0,
    val acceptedAnswers: Int = 0,
    val rank: Int = 0,
    val isCurrentUser: Boolean = false
)

enum class LeaderboardPeriod(val label: String) {
    ALL_TIME("كل الوقت"),
    THIS_MONTH("هذا الشهر"),
    THIS_WEEK("هذا الأسبوع")
}
