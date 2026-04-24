package com.eduspecial.presentation.flashcards

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.domain.model.Flashcard
import com.eduspecial.domain.model.MediaType
import com.eduspecial.domain.model.SRSResult
import com.eduspecial.domain.usecase.RecordReviewUseCase
import com.eduspecial.utils.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class StudyUiState(
    val studyQueue: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val masteredThisSession: Int = 0,
    val reviewedThisSession: Int = 0,
    val isLoading: Boolean = false,
    /** Whether TTS auto-play is enabled (user can toggle) */
    val ttsEnabled: Boolean = true,
    /** Whether TTS is currently speaking */
    val isSpeaking: Boolean = false
) {
    val currentCard: Flashcard? get() = studyQueue.getOrNull(currentIndex)
    val totalCards: Int get() = studyQueue.size
}

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val repository: FlashcardRepository,
    private val recordReviewUseCase: RecordReviewUseCase,
    val ttsManager: TtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    init {
        loadStudyQueue()

        // Observe TTS speaking state
        viewModelScope.launch {
            ttsManager.state.collect { ttsState ->
                _uiState.update {
                    it.copy(isSpeaking = ttsState == TtsManager.TtsState.SPEAKING)
                }
            }
        }
    }

    private fun loadStudyQueue() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getStudyQueue()
                .take(1)
                .collect { queue ->
                    val shuffled = queue.shuffled()
                    _uiState.update { it.copy(studyQueue = shuffled, isLoading = false) }
                    // Auto-speak the first card's term when session loads
                    shuffled.firstOrNull()?.let { card ->
                        if (_uiState.value.ttsEnabled && card.mediaType == com.eduspecial.domain.model.MediaType.NONE) {
                            speakTerm(card)
                        }
                    }
                }
        }
    }

    fun flipCard() {
        val wasFlipped = _uiState.value.isFlipped
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }

        // When flipping to back (showing definition), speak the definition
        // When flipping to front (showing term), speak the term
        val card = _uiState.value.currentCard ?: return
        if (_uiState.value.ttsEnabled) {
            if (!wasFlipped) {
                // Just flipped to back — speak definition
                if (card.mediaType == MediaType.NONE || card.mediaType == MediaType.IMAGE) {
                    ttsManager.speakDefinition(card.definition)
                }
            } else {
                // Flipped back to front — speak term again
                if (card.mediaType == MediaType.NONE) {
                    ttsManager.speakTerm(card.term)
                }
            }
        }
    }

    /**
     * Manually trigger TTS for the current card's term.
     * Called when user taps the speaker icon.
     */
    fun speakCurrentTerm() {
        val card = _uiState.value.currentCard ?: return
        ttsManager.speakTerm(card.term)
    }

    /**
     * Manually trigger TTS for the current card's definition.
     */
    fun speakCurrentDefinition() {
        val card = _uiState.value.currentCard ?: return
        ttsManager.speakDefinition(card.definition)
    }

    /**
     * Stop any ongoing speech.
     */
    fun stopSpeaking() {
        ttsManager.stop()
    }

    /**
     * Toggle TTS auto-play on/off.
     */
    fun toggleTts() {
        val newEnabled = !_uiState.value.ttsEnabled
        _uiState.update { it.copy(ttsEnabled = newEnabled) }
        if (!newEnabled) ttsManager.stop()
    }

    fun processReview(result: SRSResult) {
        val state = _uiState.value
        val currentCard = state.currentCard ?: return
        val isMastered = result is SRSResult.Easy

        // Stop any ongoing speech before moving to next card
        ttsManager.stop()

        viewModelScope.launch {
            repository.processReview(currentCard, result)
            recordReviewUseCase(archivedCount = if (isMastered) 1 else 0)
        }

        _uiState.update {
            it.copy(
                currentIndex = it.currentIndex + 1,
                isFlipped = false,
                masteredThisSession = if (isMastered) it.masteredThisSession + 1 else it.masteredThisSession,
                reviewedThisSession = it.reviewedThisSession + 1
            )
        }

        // Auto-speak the next card's term (only if no audio media attached)
        val nextCard = _uiState.value.currentCard
        if (nextCard != null && _uiState.value.ttsEnabled &&
            nextCard.mediaType == MediaType.NONE) {
            speakTerm(nextCard)
        }
    }

    fun restartSession() {
        ttsManager.stop()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getStudyQueue()
                .take(1)
                .collect { queue ->
                    val shuffled = queue.shuffled()
                    _uiState.update {
                        StudyUiState(
                            studyQueue = shuffled,
                            ttsEnabled = it.ttsEnabled
                        )
                    }
                    shuffled.firstOrNull()?.let { card ->
                        if (_uiState.value.ttsEnabled && card.mediaType == MediaType.NONE) {
                            speakTerm(card)
                        }
                    }
                }
        }
    }

    private fun speakTerm(card: Flashcard) {
        // Small delay to let the card animation complete before speaking
        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            ttsManager.speakTerm(card.term)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
