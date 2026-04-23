package com.eduspecial.data.repository

import android.util.Log
import com.eduspecial.domain.model.LeaderboardEntry
import com.eduspecial.domain.model.LeaderboardPeriod
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages leaderboard data stored in Firestore.
 *
 * Firestore structure:
 *   users/{uid} → {
 *     displayName, avatarUrl,
 *     points: Int,           ← total all-time points
 *     flashcardsAdded: Int,
 *     questionsAsked: Int,
 *     answersGiven: Int,
 *     acceptedAnswers: Int,
 *     pointsThisWeek: Int,   ← reset every Monday
 *     pointsThisMonth: Int,  ← reset every 1st of month
 *     weekKey: String,       ← "2024-W12" — used to detect week reset
 *     monthKey: String       ← "2024-04"  — used to detect month reset
 *   }
 *
 * Points system:
 *   FLASHCARD_ADDED   = +10
 *   QUESTION_ASKED    = +5
 *   ANSWER_GIVEN      = +3
 *   ANSWER_ACCEPTED   = +15
 */
@Singleton
class LeaderboardRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val usersCol = firestore.collection("users")

    companion object {
        const val POINTS_FLASHCARD  = 10
        const val POINTS_QUESTION   = 5
        const val POINTS_ANSWER     = 3
        const val POINTS_ACCEPTED   = 15
        const val LEADERBOARD_LIMIT = 50
    }

    // ─── Fetch Leaderboard ────────────────────────────────────────────────────

    /**
     * Fetches the top [LEADERBOARD_LIMIT] users sorted by points for the given period.
     * Marks the current user's entry with [LeaderboardEntry.isCurrentUser].
     */
    suspend fun getLeaderboard(period: LeaderboardPeriod): Result<List<LeaderboardEntry>> {
        return try {
            val pointsField = when (period) {
                LeaderboardPeriod.ALL_TIME   -> "points"
                LeaderboardPeriod.THIS_MONTH -> "pointsThisMonth"
                LeaderboardPeriod.THIS_WEEK  -> "pointsThisWeek"
            }

            val snap = usersCol
                .orderBy(pointsField, Query.Direction.DESCENDING)
                .limit(LEADERBOARD_LIMIT.toLong())
                .get().await()

            val currentUid = auth.currentUser?.uid
            val entries = snap.documents.mapIndexedNotNull { index, doc ->
                try {
                    val points = (doc.getLong(pointsField) ?: 0).toInt()
                    if (points <= 0 && index > 2) return@mapIndexedNotNull null // skip zero-point users after top 3
                    LeaderboardEntry(
                        userId         = doc.id,
                        displayName    = doc.getString("displayName") ?: "مستخدم",
                        avatarUrl      = doc.getString("avatarUrl"),
                        points         = points,
                        flashcardsAdded = (doc.getLong("flashcardsAdded") ?: 0).toInt(),
                        questionsAsked  = (doc.getLong("questionsAsked")  ?: 0).toInt(),
                        answersGiven    = (doc.getLong("answersGiven")    ?: 0).toInt(),
                        acceptedAnswers = (doc.getLong("acceptedAnswers") ?: 0).toInt(),
                        rank           = index + 1,
                        isCurrentUser  = doc.id == currentUid
                    )
                } catch (e: Exception) {
                    Log.w("LeaderboardRepo", "Failed to parse user ${doc.id}", e)
                    null
                }
            }

            // If current user is not in top list, fetch their entry separately
            val currentUserInList = entries.any { it.isCurrentUser }
            val finalEntries = if (!currentUserInList && currentUid != null) {
                val myEntry = fetchCurrentUserEntry(currentUid, pointsField, entries.size + 1)
                if (myEntry != null) entries + myEntry else entries
            } else entries

            Result.success(finalEntries)
        } catch (e: Exception) {
            Log.e("LeaderboardRepo", "Failed to fetch leaderboard", e)
            Result.failure(e)
        }
    }

    private suspend fun fetchCurrentUserEntry(
        uid: String,
        pointsField: String,
        estimatedRank: Int
    ): LeaderboardEntry? {
        return try {
            val doc = usersCol.document(uid).get().await()
            if (!doc.exists()) return null
            LeaderboardEntry(
                userId         = uid,
                displayName    = doc.getString("displayName") ?: "أنت",
                avatarUrl      = doc.getString("avatarUrl"),
                points         = (doc.getLong(pointsField) ?: 0).toInt(),
                flashcardsAdded = (doc.getLong("flashcardsAdded") ?: 0).toInt(),
                questionsAsked  = (doc.getLong("questionsAsked")  ?: 0).toInt(),
                answersGiven    = (doc.getLong("answersGiven")    ?: 0).toInt(),
                acceptedAnswers = (doc.getLong("acceptedAnswers") ?: 0).toInt(),
                rank           = estimatedRank,
                isCurrentUser  = true
            )
        } catch (_: Exception) { null }
    }

    // ─── Award Points ─────────────────────────────────────────────────────────

    /**
     * Awards points to the current user for adding a flashcard.
     * Also increments the flashcardsAdded counter.
     * Called from FlashcardRepository after successful creation.
     */
    suspend fun awardFlashcardPoints(userId: String) {
        awardPoints(userId, POINTS_FLASHCARD, mapOf("flashcardsAdded" to FieldValue.increment(1)))
    }

    /**
     * Awards points for posting a question.
     */
    suspend fun awardQuestionPoints(userId: String) {
        awardPoints(userId, POINTS_QUESTION, mapOf("questionsAsked" to FieldValue.increment(1)))
    }

    /**
     * Awards points for posting an answer.
     */
    suspend fun awardAnswerPoints(userId: String) {
        awardPoints(userId, POINTS_ANSWER, mapOf("answersGiven" to FieldValue.increment(1)))
    }

    /**
     * Awards bonus points when an answer is accepted as the best answer.
     */
    suspend fun awardAcceptedAnswerPoints(userId: String) {
        awardPoints(userId, POINTS_ACCEPTED, mapOf("acceptedAnswers" to FieldValue.increment(1)))
    }

    private suspend fun awardPoints(
        userId: String,
        points: Int,
        extraFields: Map<String, Any> = emptyMap()
    ) {
        if (userId.isBlank() || userId == "anonymous") return
        try {
            val weekKey  = currentWeekKey()
            val monthKey = currentMonthKey()

            val updates = mutableMapOf<String, Any>(
                "points"           to FieldValue.increment(points.toLong()),
                "pointsThisWeek"   to FieldValue.increment(points.toLong()),
                "pointsThisMonth"  to FieldValue.increment(points.toLong()),
                "weekKey"          to weekKey,
                "monthKey"         to monthKey
            )
            updates.putAll(extraFields)

            // Use set with merge to create the document if it doesn't exist
            usersCol.document(userId).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w("LeaderboardRepo", "Failed to award points to $userId", e)
        }
    }

    // ─── Period Key Helpers ───────────────────────────────────────────────────

    private fun currentWeekKey(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return "$year-W${week.toString().padStart(2, '0')}"
    }

    private fun currentMonthKey(): String {
        val cal = Calendar.getInstance()
        val year  = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return "$year-${month.toString().padStart(2, '0')}"
    }
}
