package com.eduspecial.presentation.leaderboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduspecial.data.repository.LeaderboardRepository
import com.eduspecial.domain.model.LeaderboardEntry
import com.eduspecial.domain.model.LeaderboardPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class LeaderboardUiState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPeriod: LeaderboardPeriod = LeaderboardPeriod.ALL_TIME,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard(LeaderboardPeriod.ALL_TIME)
    }

    fun selectPeriod(period: LeaderboardPeriod) {
        if (_uiState.value.selectedPeriod == period && _uiState.value.entries.isNotEmpty()) return
        _uiState.update { it.copy(selectedPeriod = period) }
        loadLeaderboard(period)
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadLeaderboard(_uiState.value.selectedPeriod, isRefresh = true)
    }

    private fun loadLeaderboard(period: LeaderboardPeriod, isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    error = null
                )
            }
            repository.getLeaderboard(period)
                .onSuccess { entries ->
                    _uiState.update {
                        it.copy(
                            entries = entries,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = "تعذّر تحميل المتصدرين. تحقق من الاتصال."
                        )
                    }
                }
        }
    }
}
