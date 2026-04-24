package com.eduspecial.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.eduspecial.data.remote.dto.UserProfileDto
import com.eduspecial.data.model.*
import com.eduspecial.data.manager.RoleManager
import com.eduspecial.utils.UserPreferencesDataStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(DelicateCoroutinesApi::class)
@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val prefs: UserPreferencesDataStore,
    private val roleManager: RoleManager
) {
    companion object {
        private const val FIRESTORE_SIDE_EFFECT_TIMEOUT_MS = 8_000L
    }

    private val usersCol = firestore.collection("users")

    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid
    fun getCurrentUserEmail(): String? = firebaseAuth.currentUser?.email
    fun getCurrentDisplayName(): String? = firebaseAuth.currentUser?.displayName
    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("No user returned")

            // Best-effort: auth must not fail if profile/security logging is unavailable.
            runCatching {
                withTimeout(FIRESTORE_SIDE_EFFECT_TIMEOUT_MS) {
                    usersCol.document(userId).update("lastLoginAt", System.currentTimeMillis()).await()
                    roleManager.logSecurityEvent(userId, SecurityEvent.LOGIN)
                }
            }

            Result.success(userId)
        } catch (e: Exception) { 
            // Log failed login attempt if we can identify the user
            try {
                val userQuery = usersCol.whereEqualTo("email", email).get().await()
                if (!userQuery.isEmpty) {
                    val userId = userQuery.documents.first().id
                    roleManager.logSecurityEvent(userId, SecurityEvent.FAILED_LOGIN, success = false)
                }
            } catch (_: Exception) { /* Ignore logging errors */ }
            
            Result.failure(e) 
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Result<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("No user returned")

            // Update Firebase Auth display name
            user.updateProfile(userProfileChangeRequest { this.displayName = displayName }).await()

            // Best-effort: keep registration successful even if profile bootstrap is delayed.
            runCatching {
                withTimeout(FIRESTORE_SIDE_EFFECT_TIMEOUT_MS) {
                    roleManager.initializeUserProfile(user.uid, email, displayName)
                }
            }

            Result.success(user.uid)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Sign in with a Google ID token obtained from GoogleSignInClient on the UI layer.
     * Creates / updates the Firestore user profile on first login.
     */
    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: throw Exception("Google sign-in returned no user")

            // Create profile on first login, otherwise just bump lastLoginAt.
            val isNewUser = result.additionalUserInfo?.isNewUser == true
            if (isNewUser) {
                roleManager.initializeUserProfile(
                    userId = user.uid,
                    email = user.email.orEmpty(),
                    displayName = user.displayName ?: user.email?.substringBefore("@") ?: "مستخدم"
                )
            } else {
                try {
                    usersCol.document(user.uid)
                        .update("lastLoginAt", System.currentTimeMillis())
                        .await()
                } catch (_: Exception) { /* ignore — profile may not exist yet */ }
            }
            roleManager.logSecurityEvent(user.uid, SecurityEvent.LOGIN)
            Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            Result.success(result.user?.uid ?: throw Exception("Anonymous sign-in failed"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getMyProfile(): UserProfileDto? {
        val uid = getCurrentUserId() ?: return null
        return try {
            val profile = roleManager.getUserProfile(uid)
            profile?.let {
                UserProfileDto(
                    uid = it.uid,
                    displayName = it.displayName,
                    email = it.email,
                    avatarUrl = it.profileImageUrl,
                    contributionCount = it.contributionCount,
                    joinedAt = it.createdAt
                )
            }
        } catch (_: Exception) { null }
    }

    /**
     * Get enhanced user profile with role and security information.
     */
    suspend fun getEnhancedProfile(): UserProfile? {
        return roleManager.getCurrentUserProfile()
    }

    /**
     * Check if current user has a specific permission.
     */
    suspend fun hasPermission(permission: Permission): Boolean {
        return roleManager.hasPermission(permission)
    }

    /**
     * Get current user's role.
     */
    suspend fun getCurrentUserRole(): UserRole {
        return roleManager.getCurrentUserRole()
    }

    suspend fun updateDisplayName(name: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("Not logged in"))
        return try {
            // Update Firestore
            usersCol.document(uid).update("displayName", name).await()
            // Update Firebase Auth
            firebaseAuth.currentUser?.updateProfile(
                userProfileChangeRequest { displayName = name }
            )?.await()
            // Update local prefs
            prefs.setDisplayName(name)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateAvatarUrl(url: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("Not logged in"))
        return try {
            usersCol.document(uid).update("avatarUrl", url).await()
            prefs.setAvatarUrl(url)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        val user = firebaseAuth.currentUser
            ?: return Result.failure(Exception("المستخدم غير مسجل الدخول"))
        val email = user.email
            ?: return Result.failure(Exception("لا يوجد بريد إلكتروني مرتبط بالحساب"))
        return try {
            // Step 1: Re-authenticate
            val credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            // Step 2: Update password
            user.updatePassword(newPassword).await()
            
            // Log security event
            roleManager.logSecurityEvent(user.uid, SecurityEvent.PASSWORD_CHANGE)
            
            Result.success(Unit)
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("كلمة المرور الحالية غير صحيحة"))
        } catch (e: com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("كلمة المرور الجديدة ضعيفة جداً (6 أحرف على الأقل)"))
        } catch (e: Exception) {
            Result.failure(Exception("فشل تغيير كلمة المرور: ${e.message}"))
        }
    }

    fun signOut() {
        val userId = getCurrentUserId()
        firebaseAuth.signOut()
        
        // Log security event (fire and forget)
        userId?.let { uid ->
            GlobalScope.launch {
                try {
                    roleManager.logSecurityEvent(uid, SecurityEvent.LOGOUT)
                } catch (_: Exception) { /* Ignore logging errors */ }
            }
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            
            // Log password reset request
            try {
                val userQuery = usersCol.whereEqualTo("email", email).get().await()
                if (!userQuery.isEmpty) {
                    val userId = userQuery.documents.first().id
                    roleManager.logSecurityEvent(userId, SecurityEvent.PASSWORD_RESET_REQUEST)
                }
            } catch (_: Exception) { /* Ignore logging errors */ }
            
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Send email verification to current user.
     */
    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("المستخدم غير مسجل الدخول")
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Check if current user's email is verified.
     */
    fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    /**
     * Reload current user to get updated verification status.
     */
    suspend fun reloadUser(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.reload()?.await()
            
            // Update verification status in Firestore
            val user = firebaseAuth.currentUser
            if (user != null && user.isEmailVerified) {
                usersCol.document(user.uid).update(mapOf(
                    "emailVerified" to true,
                    "accountStatus" to AccountStatus.ACTIVE.name
                )).await()
                
                roleManager.logSecurityEvent(user.uid, SecurityEvent.EMAIL_VERIFICATION)
            }
            
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Update user preferences.
     */
    suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("Not logged in"))
        return try {
            val prefsMap = mapOf(
                "preferences" to mapOf(
                    "language" to preferences.language,
                    "theme" to preferences.theme,
                    "notificationsEnabled" to preferences.notificationsEnabled,
                    "studyRemindersEnabled" to preferences.studyRemindersEnabled,
                    "emailNotificationsEnabled" to preferences.emailNotificationsEnabled,
                    "soundEnabled" to preferences.soundEnabled,
                    "vibrationEnabled" to preferences.vibrationEnabled,
                    "autoPlayTTS" to preferences.autoPlayTTS,
                    "dailyGoal" to preferences.dailyGoal,
                    "reminderTime" to preferences.reminderTime
                )
            )
            
            roleManager.updateUserProfile(uid, prefsMap)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Link anonymous account with email/password.
     */
    suspend fun linkAnonymousAccount(email: String, password: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("No current user")
            if (!user.isAnonymous) throw Exception("User is not anonymous")
            
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)
            user.linkWithCredential(credential).await()
            
            // Update user profile with email
            roleManager.updateUserProfile(user.uid, mapOf(
                "email" to email,
                "accountStatus" to AccountStatus.PENDING_VERIFICATION.name
            ))
            
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getIdToken(): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
        } catch (_: Exception) { null }
    }
}
