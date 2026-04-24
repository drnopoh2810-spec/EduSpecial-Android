package com.eduspecial.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduspecial.data.repository.AuthRepository
import com.eduspecial.data.repository.FlashcardRepository
import com.eduspecial.domain.model.ReviewState
import com.eduspecial.domain.usecase.ScheduleStudyReminderUseCase
import com.eduspecial.domain.usecase.UpdateDisplayNameUseCase
import com.eduspecial.domain.usecase.UploadAvatarUseCase
import com.eduspecial.utils.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val contributionCount: Int = 0,
    val masteredCount: Int = 0,
    val reviewCount: Int = 0,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val dailyGoal: Int = 10,
    val isSignedOut: Boolean = false,
    // Avatar
    val avatarUrl: String? = null,
    val isUploadingAvatar: Boolean = false,
    val avatarError: String? = null,
    // Display name editing
    val isEditingName: Boolean = false,
    val nameError: String? = null,
    val isUpdatingName: Boolean = false,
    // Password change
    val isChangingPassword: Boolean = false,
    val passwordError: String? = null,
    val passwordSuccess: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val flashcardRepository: FlashcardRepository,
    private val prefs: UserPreferencesDataStore,
    private val updateDisplayNameUseCase: UpdateDisplayNameUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val scheduleStudyReminderUseCase: ScheduleStudyReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Load local Firebase Auth data immediately
        _uiState.update {
            it.copy(
                displayName = authRepository.getCurrentDisplayName() ?: "مستخدم",
                email = authRepository.getCurrentUserEmail() ?: ""
            )
        }

        // Observe flashcard stats
        viewModelScope.launch {
            flashcardRepository.getAllFlashcards().collect { cards ->
                _uiState.update {
                    it.copy(
                        masteredCount = cards.count { c -> c.reviewState == ReviewState.ARCHIVED },
                        reviewCount = cards.count { c ->
                            c.reviewState == ReviewState.REVIEW || c.reviewState == ReviewState.LEARNING
                        }
                    )
                }
            }
        }

        // Observe preferences (dark mode, daily goal, notifications, avatar, display name)
        viewModelScope.launch {
            // Combine 5 flows using nested combine (Kotlin Flow supports up to 5 via overloads)
            combine(
                combine(prefs.isDarkTheme, prefs.dailyGoal) { dark, goal -> Pair(dark, goal) },
                combine(prefs.studyNotificationsEnabled, prefs.avatarUrl, prefs.displayName) { notif, avatar, name ->
                    Triple(notif, avatar, name)
                }
            ) { (dark, goal), (notif, avatar, name) ->
                ProfilePrefsSnapshot(dark, goal, notif, avatar, name)
            }.collect { snapshot ->
                _uiState.update {
                    it.copy(
                        isDarkMode = snapshot.isDark,
                        dailyGoal = snapshot.dailyGoal,
                        notificationsEnabled = snapshot.notificationsEnabled,
                        avatarUrl = snapshot.avatarUrl,
                        displayName = snapshot.displayName
                            ?: authRepository.getCurrentDisplayName()
                            ?: "مستخدم"
                    )
                }
            }
        }

        // Fetch fresh profile from API
        viewModelScope.launch {
            fetchProfileFromApi()
        }
    }

    private suspend fun fetchProfileFromApi() {
        try {
            val response = authRepository.getMyProfile()
            response?.let { profile ->
                _uiState.update {
                    it.copy(
                        displayName = profile.displayName.ifEmpty {
                            authRepository.getCurrentDisplayName() ?: "مستخدم"
                        },
                        email = profile.email,
                        contributionCount = profile.contributionCount
                    )
                }
            }
        } catch (e: Exception) {
            // Fallback to local data — already set in init
        }
    }

    // ─── Display Name Editing ─────────────────────────────────────────────────

    fun startEditingName() {
        _uiState.update { it.copy(isEditingName = true, nameError = null) }
    }

    fun cancelEditingName() {
        _uiState.update { it.copy(isEditingName = false, nameError = null) }
    }

    fun updateDisplayName(newName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingName = true, nameError = null) }
            val result = updateDisplayNameUseCase(newName.trim())
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isUpdatingName = false,
                            isEditingName = false,
                            displayName = newName.trim(),
                            nameError = null
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isUpdatingName = false,
                            nameError = e.message ?: "فشل تحديث الاسم"
                        )
                    }
                }
            )
        }
    }

    // ─── Avatar Upload ────────────────────────────────────────────────────────

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingAvatar = true, avatarError = null) }
            val result = uploadAvatarUseCase(uri)
            result.fold(
                onSuccess = { newUrl ->
                    _uiState.update {
                        it.copy(
                            isUploadingAvatar = false,
                            avatarUrl = newUrl,
                            avatarError = null
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isUploadingAvatar = false,
                            avatarError = "فشل رفع الصورة، حاول مرة أخرى"
                        )
                    }
                }
            )
        }
    }

    fun clearAvatarError() = _uiState.update { it.copy(avatarError = null) }

    // ─── Password Change ──────────────────────────────────────────────────────

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true, passwordError = null, passwordSuccess = false) }
            val result = authRepository.changePassword(currentPassword, newPassword)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isChangingPassword = false, passwordSuccess = true, passwordError = null)
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isChangingPassword = false, passwordError = e.message)
                    }
                }
            )
        }
    }

    fun clearPasswordState() {
        _uiState.update { it.copy(passwordError = null, passwordSuccess = false) }
    }

    // ─── Preferences ──────────────────────────────────────────────────────────

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefs.setDarkTheme(enabled) }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setStudyNotifications(enabled)
            val reminderTime = prefs.reminderTimeMillis.first()
            scheduleStudyReminderUseCase(enabled, reminderTime)
        }
    }

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch { prefs.setDailyGoal(goal) }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.update { it.copy(isSignedOut = true) }
    }
}

/** Private snapshot to combine 5 preference flows cleanly. */
private data class ProfilePrefsSnapshot(
    val isDark: Boolean,
    val dailyGoal: Int,
    val notificationsEnabled: Boolean,
    val avatarUrl: String?,
    val displayName: String?
)
