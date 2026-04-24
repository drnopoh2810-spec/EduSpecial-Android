package com.eduspecial.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduspecial.data.model.SecurityAuditLog
import com.eduspecial.data.repository.AuthRepository
import com.eduspecial.data.manager.RoleManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val roleManager: RoleManager,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()
    
    fun loadSecurityInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    val profile = roleManager.getUserProfile(userId)
                    val auditLogs = loadSecurityAuditLogs(userId)
                    
                    _uiState.value = _uiState.value.copy(
                        twoFactorEnabled = profile?.twoFactorEnabled ?: false,
                        lastLoginAt = profile?.lastLoginAt,
                        auditLogs = auditLogs,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "المستخدم غير مسجل الدخول",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "فشل في تحميل معلومات الأمان: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun toggleTwoFactorAuth(enable: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    val success = roleManager.updateUserProfile(
                        userId,
                        mapOf("twoFactorEnabled" to enable)
                    )
                    
                    if (success) {
                        _uiState.value = _uiState.value.copy(
                            twoFactorEnabled = enable,
                            isLoading = false
                        )
                        
                        // Log security event
                        roleManager.logSecurityEvent(
                            userId,
                            if (enable) com.eduspecial.data.model.SecurityEvent.TWO_FACTOR_ENABLED
                            else com.eduspecial.data.model.SecurityEvent.TWO_FACTOR_DISABLED
                        )
                        
                        // Reload audit logs
                        loadSecurityInfo()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "فشل في تحديث إعدادات المصادقة الثنائية",
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
                    error = "فشل في تحديث المصادقة الثنائية: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun signOutAllDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // In a real implementation, this would invalidate all sessions
                // For now, we'll just sign out the current user
                authRepository.signOut()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "تم تسجيل الخروج من جميع الأجهزة"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "فشل في تسجيل الخروج: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun setupAccountRecovery() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                successMessage = "إعداد استرداد الحساب غير متاح حالياً"
            )
        }
    }
    
    private suspend fun loadSecurityAuditLogs(userId: String): List<SecurityAuditLog> {
        return try {
            val snapshot = firestore.collection("security_logs")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    SecurityAuditLog(
                        id = doc.id,
                        userId = data["userId"] as? String ?: "",
                        event = com.eduspecial.data.model.SecurityEvent.valueOf(
                            data["event"] as? String ?: "LOGIN"
                        ),
                        timestamp = data["timestamp"] as? Long ?: 0,
                        ipAddress = data["ipAddress"] as? String,
                        userAgent = data["userAgent"] as? String,
                        details = data["details"] as? Map<String, Any> ?: emptyMap(),
                        success = data["success"] as? Boolean ?: true
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}

data class SecurityUiState(
    val twoFactorEnabled: Boolean = false,
    val lastLoginAt: Long? = null,
    val auditLogs: List<SecurityAuditLog> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)