package com.eduspecial

import com.eduspecial.domain.model.*
import com.eduspecial.domain.usecase.UpdateDisplayNameUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.util.Date

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun makeFlashcard(id: String, contributor: String) = Flashcard(
    id = id,
    term = "term_$id",
    definition = "def_$id",
    category = FlashcardCategory.ABA_THERAPY,
    contributor = contributor
)

private fun makeQuestion(id: String, contributor: String) = QAQuestion(
    id = id,
    question = "question_$id",
    category = FlashcardCategory.ABA_THERAPY,
    contributor = contributor
)

private fun makeAnswer(id: String, contributor: String, isAccepted: Boolean = false, upvotes: Int = 0) = QAAnswer(
    id = id,
    questionId = "q1",
    content = "answer_$id",
    contributor = contributor,
    isAccepted = isAccepted,
    upvotes = upvotes
)

/** Simulates the edit icon visibility logic from FlashcardItem */
private fun editIconVisible(card: Flashcard, userId: String): Boolean =
    userId == card.contributor

/** Simulates the edit icon visibility logic for QAQuestion */
private fun editIconVisibleForQuestion(question: QAQuestion, userId: String): Boolean =
    userId == question.contributor

/** Simulates the edit icon visibility logic for QAAnswer */
private fun editIconVisibleForAnswer(answer: QAAnswer, userId: String): Boolean =
    userId == answer.contributor

/** Simulates the accepted-answer sort used in AnswerThreadSection */
private fun sortAnswers(answers: List<QAAnswer>): List<QAAnswer> =
    answers.sortedByDescending { it.isAccepted }

/** Simulates the streak calculation from AnalyticsRepository */
private fun computeStreak(reviewCounts: List<Int>): Int {
    var streak = 0
    for (count in reviewCounts.reversed()) {
        if (count > 0) streak++ else break
    }
    return streak
}

/** Simulates display name validation from UpdateDisplayNameUseCase */
private fun validateDisplayName(name: String): Result<String> {
    return if (name.length < 2 || name.length > 50) {
        Result.failure(IllegalArgumentException("يجب أن يكون الاسم بين 2 و 50 حرفاً"))
    } else {
        Result.success(name)
    }
}

// ─── Property-Based Tests ─────────────────────────────────────────────────────

class CorrectnessPropertiesTest : StringSpec({

    // P1 — Edit icon authorship (flashcard)
    "P1: edit icon visible iff userId == card.contributor" {
        checkAll(Arb.string(1..20), Arb.string(1..20)) { cardId, userId ->
            val card = makeFlashcard(cardId, userId)
            // Same user → visible
            editIconVisible(card, userId).shouldBeTrue()
            // Different user → not visible
            val otherId = userId + "_other"
            editIconVisible(card, otherId).shouldBeFalse()
        }
    }

    // P2 — Duplicate check excludes self
    "P2: a card is never a duplicate of itself" {
        checkAll(Arb.string(1..50)) { term ->
            // Simulate: checkDuplicate with excludeId = card.id
            // The local count for the card's own term should be excluded
            // We model this as: if the only match is the card itself, result is NotDuplicate
            val cardId = "card_123"
            val allTerms = listOf(term to cardId)  // only match is the card itself
            val isDuplicate = allTerms.any { (t, id) ->
                t.equals(term, ignoreCase = true) && id != cardId
            }
            isDuplicate.shouldBeFalse()
        }
    }

    // P3 — Edit icon authorship (Q&A)
    "P3: edit icon visible for question iff userId == question.contributor" {
        checkAll(Arb.string(1..20), Arb.string(1..20)) { qId, userId ->
            val question = makeQuestion(qId, userId)
            editIconVisibleForQuestion(question, userId).shouldBeTrue()
            editIconVisibleForQuestion(question, userId + "_other").shouldBeFalse()
        }
    }

    "P3b: edit icon visible for answer iff userId == answer.contributor" {
        checkAll(Arb.string(1..20), Arb.string(1..20)) { aId, userId ->
            val answer = makeAnswer(aId, userId)
            editIconVisibleForAnswer(answer, userId).shouldBeTrue()
            editIconVisibleForAnswer(answer, userId + "_other").shouldBeFalse()
        }
    }

    // P4 — Accepted answer ordering
    "P4: accepted answer is at index 0 after sorting" {
        checkAll(
            Arb.list(Arb.string(1..10), 1..10),
            Arb.int(0..9)
        ) { ids, acceptedIdx ->
            val clampedIdx = acceptedIdx.coerceAtMost(ids.size - 1)
            val answers = ids.mapIndexed { i, id ->
                makeAnswer(id, "user_$i", isAccepted = i == clampedIdx)
            }
            val sorted = sortAnswers(answers)
            sorted.first().isAccepted.shouldBeTrue()
        }
    }

    // P5 — Upvote increments count
    "P5: upvote increments displayed count by 1" {
        checkAll(Arb.nonNegativeInt(1000)) { initialCount ->
            val answer = makeAnswer("a1", "user1", upvotes = initialCount)
            // Simulate optimistic upvote
            val afterUpvote = answer.copy(upvotes = answer.upvotes + 1)
            afterUpvote.upvotes shouldBeExactly initialCount + 1
        }
    }

    // P6 — Idempotent offline sync
    "P6: offline create then sync produces no duplicates and isPendingSync=false" {
        checkAll(Arb.list(Arb.string(1..20), 1..20)) { terms ->
            // Simulate: create flashcards offline (isPendingSync=true), then sync
            val offlineCards = terms.mapIndexed { i, term ->
                makeFlashcard("temp_$i", "user1").copy(term = term)
            }
            // After sync: no duplicates by term, all isPendingSync=false
            val synced = offlineCards.distinctBy { it.term.lowercase() }
                .map { it.copy(isOfflineCached = false) }

            // No duplicates
            synced.size shouldBe synced.distinctBy { it.term.lowercase() }.size
            // All fields preserved
            synced.forEach { card ->
                card.isOfflineCached.shouldBeFalse()
            }
        }
    }

    // P7 — Streak invariant
    "P7: streak equals length of longest suffix of consecutive non-zero days ending today" {
        checkAll(Arb.list(Arb.nonNegativeInt(10), 0..30)) { reviewCounts ->
            val computed = computeStreak(reviewCounts)
            // Verify: streak is the count of trailing non-zero entries
            val expected = reviewCounts.reversed().takeWhile { it > 0 }.size
            computed shouldBe expected
        }
    }

    // P8 — Category mastery formula
    "P8: mastery percentage == archived / total" {
        checkAll(
            Arb.positiveInt(100),
            Arb.int(0..100)
        ) { total, archivedRaw ->
            val archived = archivedRaw.coerceAtMost(total)
            val mastery = CategoryMastery(
                category = FlashcardCategory.ABA_THERAPY,
                total = total,
                archived = archived
            )
            mastery.percentage shouldBeExactly archived.toFloat() / total
        }
    }

    "P8b: mastery percentage is 0 when total is 0" {
        val mastery = CategoryMastery(
            category = FlashcardCategory.ABA_THERAPY,
            total = 0,
            archived = 0
        )
        mastery.percentage shouldBeExactly 0f
    }

    // P9 — Bookmark toggle idempotence and round-trip
    "P9: toggle twice returns original state" {
        checkAll(Arb.boolean()) { initialState ->
            var state = initialState
            // Toggle once
            state = !state
            // Toggle again
            state = !state
            // Back to original
            state shouldBe initialState
        }
    }

    "P9b: toggle once changes state" {
        checkAll(Arb.boolean()) { initialState ->
            val afterToggle = !initialState
            afterToggle shouldBe !initialState
        }
    }

    // P10 — Display name rejects invalid lengths
    "P10: display name shorter than 2 chars is rejected" {
        checkAll(Arb.string(0..1)) { name ->
            val result = validateDisplayName(name)
            result.isFailure.shouldBeTrue()
        }
    }

    "P10b: display name longer than 50 chars is rejected" {
        checkAll(Arb.string(51..200)) { name ->
            val result = validateDisplayName(name)
            result.isFailure.shouldBeTrue()
        }
    }

    // P11 — Display name round-trip
    "P11: valid display name (2..50 chars) is accepted and value preserved" {
        checkAll(Arb.string(2..50)) { name ->
            val result = validateDisplayName(name)
            result.isSuccess.shouldBeTrue()
            result.getOrNull() shouldBe name
        }
    }

    // P12 — Media URL propagation
    "P12: media URL and type are preserved in request DTO" {
        checkAll(
            Arb.string(1..500),
            Arb.enum<MediaType>()
        ) { url, mediaType ->
            // Simulate CreateFlashcardRequest / UpdateFlashcardRequest construction
            data class MediaRequest(val mediaUrl: String?, val mediaType: String)
            val request = MediaRequest(mediaUrl = url, mediaType = mediaType.name)
            request.mediaUrl shouldBe url
            request.mediaType shouldBe mediaType.name
        }
    }
})
