package com.eduspecial.presentation.home

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.domain.model.CategoryMastery
import com.eduspecial.domain.model.DailyProgress
import com.eduspecial.domain.model.ReviewState
import com.eduspecial.domain.usecase.GetCategoryMasteryUseCase
import com.eduspecial.domain.usecase.GetStudyStreakUseCase
import com.eduspecial.domain.usecase.GetWeeklyProgressUseCase
import com.eduspecial.data.repository.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class HomeStats(
    val totalFlashcards: Int = 0,
    val mastered: Int = 0,
    val toReview: Int = 0
)

@Stable
data class HomeCategoryItem(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
) {
    companion object {
        val allCategories = listOf(
            HomeCategoryItem("تحليل السلوك التطبيقي", "تقنيات ABA وتطبيقاتها", Icons.Default.Psychology, Color(0xFF1565C0)),
            HomeCategoryItem("طيف التوحد", "تعريفات ASD والتدخلات", Icons.Default.Favorite, Color(0xFF6A1B9A)),
            HomeCategoryItem("المعالجة الحسية", "التكامل الحسي والتنظيم", Icons.Default.TouchApp, Color(0xFF00897B)),
            HomeCategoryItem("النطق واللغة", "علاج التواصل واللغة", Icons.Default.RecordVoiceOver, Color(0xFFE65100)),
            HomeCategoryItem("العلاج الوظيفي", "أهداف OT وأساليبه", Icons.Default.AccessibilityNew, Color(0xFF2E7D32)),
            HomeCategoryItem("التدخل السلوكي", "استراتيجيات دعم السلوك", Icons.Default.Assignment, Color(0xFFC62828)),
            HomeCategoryItem("التعليم الشامل", "مفاهيم الدمج وخطط IEP", Icons.Default.School, Color(0xFF00838F)),
            HomeCategoryItem("الإعاقات النمائية", "تصنيفات DD والدعم", Icons.Default.ChildCare, Color(0xFFAD1457)),
            HomeCategoryItem("أدوات التقييم", "أدوات القياس والتقييم", Icons.Default.Assessment, Color(0xFF4527A0)),
            HomeCategoryItem("دعم الأسرة", "إرشادات مقدمي الرعاية", Icons.Default.FamilyRestroom, Color(0xFF558B2F))
        )
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val getStudyStreak: GetStudyStreakUseCase,
    private val getWeeklyProgress: GetWeeklyProgressUseCase,
    private val getCategoryMastery: GetCategoryMasteryUseCase,
    private val userPreferencesDataStore: com.eduspecial.utils.UserPreferencesDataStore
) : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    val stats: StateFlow<HomeStats> = flashcardRepository
        .getAllFlashcards()
        .map { cards ->
            HomeStats(
                totalFlashcards = cards.size,
                mastered = cards.count { it.reviewState == ReviewState.ARCHIVED },
                toReview = cards.count {
                    it.reviewState == ReviewState.REVIEW || it.reviewState == ReviewState.LEARNING
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeStats())

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _weeklyProgress = MutableStateFlow<List<DailyProgress>>(emptyList())
    val weeklyProgress: StateFlow<List<DailyProgress>> = _weeklyProgress.asStateFlow()

    private val _categoryMastery = MutableStateFlow<List<CategoryMastery>>(emptyList())
    val categoryMastery: StateFlow<List<CategoryMastery>> = _categoryMastery.asStateFlow()

    private val _todayReviewed = MutableStateFlow(0)
    val todayReviewed: StateFlow<Int> = _todayReviewed.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val dailyGoal: StateFlow<Int> = userPreferencesDataStore.dailyGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10)

    init {
        viewModelScope.launch {
            flashcardRepository.refreshFromServer()
        }
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val analyticsSnapshot = fetchAnalyticsSnapshot()
                applyAnalyticsSnapshot(analyticsSnapshot)
            } catch (e: Exception) {
                // Keep defaults when analytics fetch fails; log for debugging.
                Log.w(TAG, "loadAnalytics failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchAnalyticsSnapshot(): AnalyticsSnapshot {
        val streakDeferred = viewModelScope.async { getStudyStreak() }
        val weeklyProgressDeferred = viewModelScope.async { getWeeklyProgress() }
        val categoryMasteryDeferred = viewModelScope.async { getCategoryMastery() }
        val todayReviewedDeferred = viewModelScope.async { analyticsRepository.getTodayReviewCount() }

        return AnalyticsSnapshot(
            streak = streakDeferred.await(),
            weeklyProgress = weeklyProgressDeferred.await(),
            categoryMastery = categoryMasteryDeferred.await(),
            todayReviewed = todayReviewedDeferred.await()
        )
    }

    private fun applyAnalyticsSnapshot(snapshot: AnalyticsSnapshot) {
        _streak.value = snapshot.streak
        _weeklyProgress.value = snapshot.weeklyProgress
        _categoryMastery.value = snapshot.categoryMastery
        _todayReviewed.value = snapshot.todayReviewed
    }
}

private data class AnalyticsSnapshot(
    val streak: Int,
    val weeklyProgress: List<DailyProgress>,
    val categoryMastery: List<CategoryMastery>,
    val todayReviewed: Int
)
