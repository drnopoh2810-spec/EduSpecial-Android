package com.eduspecial.presentation.qa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduspecial.data.repository.AuthRepository
import com.eduspecial.data.repository.BookmarkRepository
import com.eduspecial.data.repository.DuplicateCheckResult
import com.eduspecial.data.repository.QARepository
import com.eduspecial.domain.model.BookmarkType
import com.eduspecial.domain.model.FlashcardCategory
import com.eduspecial.domain.model.QAQuestion
import com.eduspecial.domain.usecase.AcceptAnswerUseCase
import com.eduspecial.domain.usecase.EditAnswerUseCase
import com.eduspecial.domain.usecase.EditQuestionUseCase
import com.eduspecial.domain.usecase.ToggleBookmarkUseCase
import com.eduspecial.domain.usecase.UpvoteAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class QAUiState(
    val newQuestion: String = "",
    val newAnswer: String = "",
    val newCategory: FlashcardCategory = FlashcardCategory.ABA_THERAPY,
    val isDuplicate: Boolean = false,
    val isCheckingDuplicate: Boolean = false,
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = false,
    val showUnansweredOnly: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class QAViewModel @Inject constructor(
    private val repository: QARepository,
    private val authRepository: AuthRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val editQuestionUseCase: EditQuestionUseCase,
    private val editAnswerUseCase: EditAnswerUseCase,
    private val acceptAnswerUseCase: AcceptAnswerUseCase,
    private val upvoteAnswerUseCase: UpvoteAnswerUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QAUiState())
    val uiState: StateFlow<QAUiState> = _uiState.asStateFlow()

    private val _showUnanswered = MutableStateFlow(false)

    val questions: StateFlow<List<QAQuestion>> = _showUnanswered.flatMapLatest { unanswered ->
        if (unanswered) repository.getUnansweredQuestions()
        else repository.getAllQuestions()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** The ID of the question whose answer thread is currently expanded inline. */
    private val _expandedQuestionId = MutableStateFlow<String?>(null)
    val expandedQuestionId: StateFlow<String?> = _expandedQuestionId.asStateFlow()

    /** Set of question IDs that the current user has bookmarked. */
    val bookmarkedQuestionIds: StateFlow<Set<String>> = bookmarkRepository
        .getBookmarkedQuestionIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    /** The currently authenticated user's ID — used for author-gated actions. */
    val currentUserId: String
        get() = authRepository.getCurrentUserId() ?: ""

    private var duplicateCheckJob: Job? = null

    init {
        viewModelScope.launch { repository.refreshFromServer() }
    }

    fun showAll() { _showUnanswered.value = false }
    fun showUnanswered() { _showUnanswered.value = true }

    fun toggleExpanded(questionId: String) {
        _expandedQuestionId.value =
            if (_expandedQuestionId.value == questionId) null else questionId
    }

    fun onQuestionChange(q: String) {
        _uiState.update { it.copy(newQuestion = q, isDuplicate = false) }
        duplicateCheckJob?.cancel()
        if (q.length >= 10) {
            duplicateCheckJob = viewModelScope.launch {
                delay(600)
                _uiState.update { it.copy(isCheckingDuplicate = true) }
                val result = repository.checkDuplicate(q.trim())
                _uiState.update {
                    it.copy(
                        isDuplicate = result is DuplicateCheckResult.IsDuplicate,
                        isCheckingDuplicate = false
                    )
                }
            }
        }
    }

    fun onAnswerChange(answer: String) = _uiState.update { it.copy(newAnswer = answer) }

    fun onCategoryChange(cat: FlashcardCategory) = _uiState.update { it.copy(newCategory = cat) }

    fun submitQuestion() {
        val state = _uiState.value
        if (state.newQuestion.isBlank()) return
        val contributorId = authRepository.getCurrentUserId() ?: "anonymous"
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            val result = repository.createQuestion(
                question = state.newQuestion.trim(),
                category = state.newCategory,
                contributorId = contributorId,
                tags = emptyList()
            )
            result.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    newQuestion = "",
                    newCategory = FlashcardCategory.ABA_THERAPY
                )
            }
        }
    }

    fun submitAnswer(questionId: String) {
        val state = _uiState.value
        if (state.newAnswer.isBlank()) return
        val contributorId = authRepository.getCurrentUserId() ?: "anonymous"
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            val result = repository.createAnswer(
                questionId = questionId,
                content = state.newAnswer.trim(),
                contributorId = contributorId
            )
            result.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            _uiState.update { it.copy(isSubmitting = false, newAnswer = "") }
        }
    }

    fun editQuestion(id: String, question: String, category: FlashcardCategory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = editQuestionUseCase(id, question, category)
            result.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun editAnswer(id: String, content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = editAnswerUseCase(id, content)
            result.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun acceptAnswer(answerId: String, questionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = acceptAnswerUseCase(answerId, questionId)
            result.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun upvoteAnswer(answerId: String) {
        viewModelScope.launch {
            // Optimistic update is handled at the repository/DAO level
            upvoteAnswerUseCase(answerId)
        }
    }

    fun upvoteQuestion(id: String) {
        viewModelScope.launch {
            repository.upvoteQuestion(id)
        }
    }

    fun toggleBookmark(questionId: String) {
        viewModelScope.launch {
            toggleBookmarkUseCase(questionId, BookmarkType.QUESTION)
        }
    }

    fun refreshFromServer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.refreshFromServer()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
