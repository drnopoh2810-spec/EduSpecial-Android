package com.eduspecial.presentation.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduspecial.data.remote.search.AlgoliaSearchService
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.data.repository.QARepository
import com.eduspecial.domain.model.SearchResult
import com.eduspecial.domain.model.SearchResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Immutable
data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val filterType: SearchResultType? = null,
    val error: String? = null,
    /** True when results come from local Room search (Algolia unavailable) */
    val isLocalResults: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val qaRepository: QARepository,
    private val algoliaSearchService: AlgoliaSearchService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Initialize Algolia (config loaded from Remote Config)
        viewModelScope.launch { algoliaSearchService.initialize() }
    }

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, isLoading = query.length >= 2) }
        searchJob?.cancel()

        if (query.length < 2) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // debounce — wait for user to stop typing
            performSearch(query)
        }
    }

    fun clearQuery() {
        searchJob?.cancel()
        _uiState.update { SearchUiState() }
    }

    fun setFilter(type: SearchResultType?) {
        _uiState.update { it.copy(filterType = type) }
        val q = _uiState.value.query
        if (q.length >= 2) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch { performSearch(q) }
        }
    }

    private suspend fun performSearch(query: String) {
        val filterType = _uiState.value.filterType

        val algoliaResults = tryAlgoliaSearch(query, filterType)

        if (algoliaResults != null && algoliaResults.isNotEmpty()) {
            _uiState.update {
                it.copy(results = algoliaResults, isLoading = false, error = null, isLocalResults = false)
            }
            return
        }

        val localResults = performLocalSearch(query, filterType)
        _uiState.update {
            it.copy(
                results = localResults,
                isLoading = false,
                error = null,
                isLocalResults = algoliaResults != null
            )
        }
    }

    private suspend fun tryAlgoliaSearch(
        query: String,
        filterType: SearchResultType?
    ): List<SearchResult>? {
        if (!algoliaSearchService.isAvailable()) return null
        return try {
            val results = mutableListOf<SearchResult>()

            if (filterType == null || filterType == SearchResultType.FLASHCARD) {
                algoliaSearchService.searchFlashcards(query, limit = 15)
                    .getOrDefault(emptyList())
                    .forEach { card ->
                        results.add(
                            SearchResult(
                                id = card.id,
                                type = SearchResultType.FLASHCARD,
                                title = card.term,
                                subtitle = card.definition
                            )
                        )
                    }
            }

            if (filterType == null || filterType == SearchResultType.QUESTION) {
                algoliaSearchService.searchQuestions(query, limit = 10)
                    .getOrDefault(emptyList())
                    .forEach { q ->
                        results.add(
                            SearchResult(
                                id = q.id,
                                type = SearchResultType.QUESTION,
                                title = q.question,
                                subtitle = q.category.name.replace("_", " ")
                            )
                        )
                    }
            }

            results
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun performLocalSearch(
        query: String,
        filterType: SearchResultType?
    ): List<SearchResult> {
        val results = mutableListOf<SearchResult>()

        if (filterType == null || filterType == SearchResultType.FLASHCARD) {
            flashcardRepository.searchLocal(query).forEach { card ->
                results.add(
                    SearchResult(
                        id       = card.id,
                        type     = SearchResultType.FLASHCARD,
                        title    = card.term,
                        subtitle = card.definition
                    )
                )
            }
        }

        if (filterType == null || filterType == SearchResultType.QUESTION) {
            qaRepository.searchLocal(query).forEach { question ->
                results.add(
                    SearchResult(
                        id       = question.id,
                        type     = SearchResultType.QUESTION,
                        title    = question.question,
                        subtitle = question.category.name.replace("_", " ")
                    )
                )
            }
        }

        return results
    }
}
