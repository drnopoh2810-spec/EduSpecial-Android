package com.eduspecial.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduspecial.data.manager.RoleManager
import com.eduspecial.data.model.UserPreferences
import com.eduspecial.data.model.UserProfile
import com.eduspecial.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val roleManager: RoleManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSettingsUiState())
    val uiState: StateFlow<ProfileSettingsUiState> = _uiState.asStateFlow()

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val profile = roleManager.getCurrentUserProfile()
                _uiState.value = _uiState.value.copy(
                    userProfile = profile,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "فشل في تحميل الملف الشخصي: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun updateProfile(updates: Map<String, Any>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    val success = roleManager.updateUserProfile(userId, updates)
                    if (success) {
                        loadUserProfile()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "فشل في تحديث الملف الشخصي",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "المستخدم غير مسجل الدخول",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "فشل في تحديث الملف الشخصي: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun updatePreferences(preferences: UserPreferences) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = authRepository.updateUserPreferences(preferences)
                result.fold(
                    onSuccess = {
                        loadUserProfile()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "فشل في تحديث التفضيلات: ${error.message}",
                            isLoading = false
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "فشل في تحديث التفضيلات: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = authRepository.changePassword(currentPassword, newPassword)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "تم تغيير كلمة المرور بنجاح"
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "فشل في تغيير كلمة المرور",
                            isLoading = false
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "فشل في تغيير كلمة المرور: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                _uiState.value = _uiState.value.copy(
                    error = "حذف الحساب غير متاح حالياً. يرجى التواصل مع الدعم الفني.",
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "فشل في حذف الحساب: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}

data class ProfileSettingsUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
