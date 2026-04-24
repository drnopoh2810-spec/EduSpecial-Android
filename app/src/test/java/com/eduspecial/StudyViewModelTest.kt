package com.eduspecial

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.domain.model.*
import com.eduspecial.domain.usecase.RecordReviewUseCase
import com.eduspecial.presentation.flashcards.StudyViewModel
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class StudyViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FlashcardRepository
    private lateinit var recordReviewUseCase: RecordReviewUseCase
    private lateinit var viewModel: StudyViewModel

    private fun makeCard(id: String) = Flashcard(
        id = id,
        term = "Term $id",
        definition = "Definition $id",
        category = FlashcardCategory.ABA_THERAPY,
        contributor = "user1",
        reviewState = ReviewState.REVIEW,
        nextReviewDate = Date(System.currentTimeMillis() - 1000)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        recordReviewUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(cards: List<Flashcard> = emptyList()): StudyViewModel {
        whenever(repository.getStudyQueue()).thenReturn(flowOf(cards))
        return StudyViewModel(repository, recordReviewUseCase)
    }

    // ─── Initial State ────────────────────────────────────────────────────────

    @Test
    fun `initial state has empty queue and no current card`() = runTest {
        viewModel = createViewModel(emptyList())
        val state = viewModel.uiState.value
        state.studyQueue.isEmpty().shouldBeTrue()
        state.currentCard.shouldBeNull()
        state.isFlipped.shouldBeFalse()
        state.reviewedThisSession shouldBeExactly 0
        state.masteredThisSession shouldBeExactly 0
    }

    @Test
    fun `initial state with cards sets currentCard`() = runTest {
        val cards = listOf(makeCard("1"), makeCard("2"), makeCard("3"))
        viewModel = createViewModel(cards)
        viewModel.uiState.value.currentCard.shouldNotBeNull()
        viewModel.uiState.value.totalCards shouldBeExactly 3
    }

    // ─── Flip ─────────────────────────────────────────────────────────────────

    @Test
    fun `flipCard toggles isFlipped`() = runTest {
        viewModel = createViewModel(listOf(makeCard("1")))
        viewModel.uiState.value.isFlipped.shouldBeFalse()
        viewModel.flipCard()
        viewModel.uiState.value.isFlipped.shouldBeTrue()
        viewModel.flipCard()
        viewModel.uiState.value.isFlipped.shouldBeFalse()
    }

    // ─── Review Processing ────────────────────────────────────────────────────

    @Test
    fun `processReview advances to next card`() = runTest {
        val cards = listOf(makeCard("1"), makeCard("2"))
        viewModel = createViewModel(cards)
        val firstCard = viewModel.uiState.value.currentCard
        viewModel.processReview(SRSResult.Good)
        val secondCard = viewModel.uiState.value.currentCard
        firstCard?.id shouldBe cards[0].id
        secondCard?.id shouldBe cards[1].id
    }

    @Test
    fun `processReview increments reviewedThisSession`() = runTest {
        viewModel = createViewModel(listOf(makeCard("1"), makeCard("2")))
        viewModel.processReview(SRSResult.Good)
        viewModel.uiState.value.reviewedThisSession shouldBeExactly 1
        viewModel.processReview(SRSResult.Hard)
        viewModel.uiState.value.reviewedThisSession shouldBeExactly 2
    }

    @Test
    fun `processReview with Easy increments masteredThisSession`() = runTest {
        viewModel = createViewModel(listOf(makeCard("1"), makeCard("2")))
        viewModel.processReview(SRSResult.Easy)
        viewModel.uiState.value.masteredThisSession shouldBeExactly 1
    }

    @Test
    fun `processReview with Good does NOT increment masteredThisSession`() = runTest {
        viewModel = createViewModel(listOf(makeCard("1")))
        viewModel.processReview(SRSResult.Good)
        viewModel.uiState.value.masteredThisSession shouldBeExactly 0
    }

    @Test
    fun `processReview resets isFlipped to false`() = runTest {
        viewModel = createViewModel(listOf(makeCard("1"), makeCard("2")))
        viewModel.flipCard()
        viewModel.uiState.value.isFlipped.shouldBeTrue()
        viewModel.processReview(SRSResult.Good)
        viewModel.uiState.value.isFlipped.shouldBeFalse()
    }

    @Test
    fun `processReview on last card sets currentCard to null`() = runTest {
        viewModel = createViewModel(listOf(makeCard("1")))
        viewModel.processReview(SRSResult.Good)
        viewModel.uiState.value.currentCard.shouldBeNull()
    }

    @Test
    fun `processReview calls repository processReview`() = runTest {
        val card = makeCard("1")
        viewModel = createViewModel(listOf(card))
        viewModel.processReview(SRSResult.Good)
        verify(repository).processReview(any(), eq(SRSResult.Good))
    }

    @Test
    fun `processReview calls recordReviewUseCase`() = runTest {
        viewModel = createViewModel(listOf(makeCard("1")))
        viewModel.processReview(SRSResult.Easy)
        verify(recordReviewUseCase).invoke(archivedCount = 1)
    }

    // ─── Session Complete ─────────────────────────────────────────────────────

    @Test
    fun `session complete when all cards reviewed`() = runTest {
        val cards = listOf(makeCard("1"), makeCard("2"))
        viewModel = createViewModel(cards)
        viewModel.processReview(SRSResult.Good)
        viewModel.processReview(SRSResult.Good)
        viewModel.uiState.value.currentCard.shouldBeNull()
        viewModel.uiState.value.reviewedThisSession shouldBeExactly 2
    }

    // ─── Restart ──────────────────────────────────────────────────────────────

    @Test
    fun `restartSession resets state`() = runTest {
        val cards = listOf(makeCard("1"))
        viewModel = createViewModel(cards)
        viewModel.processReview(SRSResult.Good)
        viewModel.uiState.value.currentCard.shouldBeNull()

        // Restart
        whenever(repository.getStudyQueue()).thenReturn(flowOf(cards))
        viewModel.restartSession()

        viewModel.uiState.value.reviewedThisSession shouldBeExactly 0
        viewModel.uiState.value.masteredThisSession shouldBeExactly 0
        viewModel.uiState.value.isFlipped.shouldBeFalse()
    }
}
