package com.eduspecial.presentation.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eduspecial.data.repository.BookmarkRepository
import com.eduspecial.data.repository.DuplicateCheckResult
import com.eduspecial.data.repository.FlashcardPagingRepository
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.data.repository.AuthRepository
import com.eduspecial.domain.model.BookmarkType
import com.eduspecial.domain.model.Flashcard
import com.eduspecial.domain.model.FlashcardCategory
import com.eduspecial.domain.model.MediaType
import com.eduspecial.domain.usecase.EditFlashcardUseCase
import com.eduspecial.domain.usecase.ToggleBookmarkUseCase
import androidx.compose.runtime.Immutable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
@Immutable
data class FlashcardsUiState(
    val newTerm: String = "",
    val newDefinition: String = "",
    val newCategory: FlashcardCategory = FlashcardCategory.ABA_THERAPY,
    val selectedCategory: FlashcardCategory? = null,
    val isDuplicate: Boolean = false,
    val isCheckingDuplicate: Boolean = false,
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val undoFlashcard: Flashcard? = null  // card pending undo-delete
)

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val repository: FlashcardRepository,
    private val authRepository: AuthRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val editFlashcardUseCase: EditFlashcardUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val pagingRepository: FlashcardPagingRepository,
    val ttsManager: com.eduspecial.utils.TtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardsUiState())
    val uiState: StateFlow<FlashcardsUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<FlashcardCategory?>(null)

    /** The currently authenticated user's ID — used for author-gated actions. */
    val currentUserId: String
        get() = authRepository.getCurrentUserId() ?: ""

    /**
     * Paged flashcard stream — loads [PAGE_SIZE] items at a time.
     * Backed by Room (offline-first) with RemoteMediator fetching from API.
     * cachedIn(viewModelScope) survives configuration changes.
     */
    val flashcardsPaged: Flow<PagingData<Flashcard>> = _selectedCategory
        .flatMapLatest { category ->
            pagingRepository.getFlashcardsPaged(category?.name)
        }
        .cachedIn(viewModelScope)

    /**
     * Non-paged flow kept for backward compatibility with existing composables
     * that haven't been migrated to LazyPagingItems yet.
     */
    val flashcards: StateFlow<List<Flashcard>> = combine(
        repository.getAllFlashcards(),
        _selectedCategory
    ) { cards, category ->
        if (category == null) cards
        else cards.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Set of flashcard IDs that the current user has bookmarked. */
    val bookmarkedIds: StateFlow<Set<String>> = bookmarkRepository
        .getBookmarkedFlashcardIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private var duplicateCheckJob: Job? = null
    private var undoDeleteJob: Job? = null

    fun onTermChange(term: String) {
        _uiState.update { it.copy(newTerm = term, isDuplicate = false) }
        duplicateCheckJob?.cancel()
        if (term.length >= 3) {
            duplicateCheckJob = viewModelScope.launch {
                delay(500)
                _uiState.update { it.copy(isCheckingDuplicate = true) }
                val result = repository.checkDuplicate(term.trim())
                _uiState.update {
                    it.copy(
                        isDuplicate = result is DuplicateCheckResult.IsDuplicate,
                        isCheckingDuplicate = false
                    )
                }
            }
        }
    }

    fun onDefinitionChange(def: String) = _uiState.update { it.copy(newDefinition = def) }
    fun onCategoryChange(cat: FlashcardCategory) = _uiState.update { it.copy(newCategory = cat) }

    fun filterByCategory(cat: FlashcardCategory?) {
        _selectedCategory.value = cat
        _uiState.update { it.copy(selectedCategory = cat) }
    }

    fun submitFlashcard() {
        submitFlashcardWithMedia(null, MediaType.NONE)
    }

    fun submitFlashcardWithMedia(mediaUrl: String?, mediaType: MediaType) {
        val state = _uiState.value
        if (state.newTerm.isBlank() || state.newDefinition.isBlank()) return

        val contributorId = authRepository.getCurrentUserId() ?: "anonymous"

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            val result = repository.createFlashcard(
                term = state.newTerm.trim(),
                definition = state.newDefinition.trim(),
                category = state.newCategory,
                mediaUrl = mediaUrl,
                mediaType = mediaType,
                contributorId = contributorId
            )
            result.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    newTerm = "",
                    newDefinition = "",
                    newCategory = FlashcardCategory.ABA_THERAPY
                )
            }
        }
    }

    fun editFlashcard(
        id: String,
        term: String,
        definition: String,
        category: FlashcardCategory,
        mediaUrl: String?,
        mediaType: MediaType
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = editFlashcardUseCase(id, term, definition, category, mediaUrl, mediaType)
            result.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun toggleBookmark(flashcardId: String) {
        viewModelScope.launch {
            toggleBookmarkUseCase(flashcardId, BookmarkType.FLASHCARD)
        }
    }

    /**
     * Soft-delete: removes the card locally and starts a 4-second undo window.
     * If [undoDelete] is not called within that window, the deletion is committed to the server.
     */
    fun deleteFlashcard(flashcard: Flashcard) {
        // Cancel any previous pending undo
        undoDeleteJob?.cancel()

        _uiState.update { it.copy(undoFlashcard = flashcard) }

        undoDeleteJob = viewModelScope.launch {
            delay(4_000)
            // Undo window expired — commit the deletion
            _uiState.update { it.copy(undoFlashcard = null) }
            repository.deleteFlashcard(flashcard.id)
        }
    }

    /** Call within 4 seconds of [deleteFlashcard] to cancel the deletion. */
    fun undoDelete() {
        undoDeleteJob?.cancel()
        undoDeleteJob = null
        _uiState.update { it.copy(undoFlashcard = null) }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    /** Speak a flashcard term using TTS */
    fun speakTerm(term: String) {
        ttsManager.speakTerm(term)
    }
}
