package com.eduspecial.data.manager

import android.util.Log
import com.eduspecial.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    companion object {
        private const val TAG = "RoleManager"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_SECURITY_LOGS = "security_logs"
        private const val COLLECTION_ROLE_ASSIGNMENTS = "role_assignments"

        // Bootstrapping admins: emails listed here are auto-promoted to ADMIN on
        // first sign-in and kept at ADMIN even if the Firestore doc is missing
        // a role field. Lower-cased for case-insensitive matching.
        private val ADMIN_EMAILS: Set<String> = setOf(
            "mahmoudnabihsaleh@gmail.com"
        )
    }
    
    /**
     * Get the current user's role.
     */
    suspend fun getCurrentUserRole(): UserRole {
        val currentUser = auth.currentUser ?: return UserRole.USER
        // Bootstrap: if the signed-in email is in the admin allow-list, treat as ADMIN
        // even if Firestore is unreachable or the document hasn't been initialized yet.
        if (currentUser.email?.lowercase() in ADMIN_EMAILS) return UserRole.ADMIN
        return getUserRole(currentUser.uid)
    }
    
    /**
     * Get a specific user's role.
     */
    suspend fun getUserRole(userId: String): UserRole {
        return try {
            val userDoc = firestore.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            if (userDoc.exists()) {
                val roleString = userDoc.getString("role")
                val role = UserRole.fromString(roleString)
                // Belt-and-braces: ensure admin bootstrap email always wins.
                val email = userDoc.getString("email")?.lowercase()
                if (email in ADMIN_EMAILS) UserRole.ADMIN else role
            } else {
                UserRole.USER // Default role for new users
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user role: ${e.message}")
            UserRole.USER
        }
    }
    
    /**
     * Get the current user's complete profile.
     */
    suspend fun getCurrentUserProfile(): UserProfile? {
        val currentUser = auth.currentUser ?: return null
        return getUserProfile(currentUser.uid)
    }
    
    /**
     * Get a specific user's complete profile.
     */
    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val userDoc = firestore.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            if (userDoc.exists()) {
                val data = userDoc.data ?: return null
                
                UserProfile(
                    uid = userId,
                    email = data["email"] as? String ?: "",
                    displayName = data["displayName"] as? String ?: "",
                    role = UserRole.fromString(data["role"] as? String),
                    accountStatus = AccountStatus.fromString(data["accountStatus"] as? String),
                    emailVerified = data["emailVerified"] as? Boolean ?: false,
                    phoneVerified = data["phoneVerified"] as? Boolean ?: false,
                    twoFactorEnabled = data["twoFactorEnabled"] as? Boolean ?: false,
                    createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                    lastLoginAt = data["lastLoginAt"] as? Long ?: System.currentTimeMillis(),
                    profileImageUrl = data["profileImageUrl"] as? String,
                    bio = data["bio"] as? String,
                    points = (data["points"] as? Long)?.toInt() ?: 0,
                    contributionCount = (data["contributionCount"] as? Long)?.toInt() ?: 0,
                    moderationScore = (data["moderationScore"] as? Double)?.toFloat() ?: 0.5f,
                    preferences = parseUserPreferences(data["preferences"] as? Map<String, Any>)
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user profile: ${e.message}")
            null
        }
    }
    
    /**
     * Check if the current user has a specific permission.
     */
    suspend fun hasPermission(permission: Permission): Boolean {
        val userRole = getCurrentUserRole()
        return permission.isGrantedTo(userRole)
    }
    
    /**
     * Check if a specific user has a permission.
     */
    suspend fun userHasPermission(userId: String, permission: Permission): Boolean {
        val userRole = getUserRole(userId)
        return permission.isGrantedTo(userRole)
    }
    
    /**
     * Assign a role to a user (Admin only).
     */
    suspend fun assignRole(
        targetUserId: String, 
        newRole: UserRole,
        reason: String = ""
    ): Boolean {
        return try {
            // Check if current user has permission to assign roles
            if (!hasPermission(Permission.ASSIGN_ROLES)) {
                Log.w(TAG, "User does not have permission to assign roles")
                return false
            }
            
            val currentUserId = auth.currentUser?.uid ?: return false
            val oldRole = getUserRole(targetUserId)
            
            // Update user role
            firestore.collection(COLLECTION_USERS)
                .document(targetUserId)
                .update(mapOf(
                    "role" to newRole.name,
                    "updatedAt" to System.currentTimeMillis(),
                    "updatedBy" to currentUserId
                ))
                .await()
            
            // Log role assignment
            logRoleAssignment(currentUserId, targetUserId, oldRole, newRole, reason)
            
            // Log security event
            logSecurityEvent(
                targetUserId,
                SecurityEvent.ROLE_CHANGE,
                mapOf(
                    "oldRole" to oldRole.name,
                    "newRole" to newRole.name,
                    "assignedBy" to currentUserId,
                    "reason" to reason
                )
            )
            
            Log.d(TAG, "✅ Role assigned: $targetUserId -> ${newRole.name}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to assign role: ${e.message}")
            false
        }
    }
    
    /**
     * Update account status (Admin/Moderator only).
     */
    suspend fun updateAccountStatus(
        targetUserId: String,
        newStatus: AccountStatus,
        reason: String = ""
    ): Boolean {
        return try {
            // Check permissions
            val requiredPermission = when (newStatus) {
                AccountStatus.SUSPENDED, AccountStatus.BANNED -> Permission.MANAGE_USERS
                AccountStatus.ACTIVE -> Permission.MANAGE_USERS
                AccountStatus.PENDING_VERIFICATION -> Permission.MANAGE_USERS
            }
            
            if (!hasPermission(requiredPermission)) {
                Log.w(TAG, "User does not have permission to update account status")
                return false
            }
            
            val currentUserId = auth.currentUser?.uid ?: return false
            
            // Update account status
            firestore.collection(COLLECTION_USERS)
                .document(targetUserId)
                .update(mapOf(
                    "accountStatus" to newStatus.name,
                    "updatedAt" to System.currentTimeMillis(),
                    "updatedBy" to currentUserId
                ))
                .await()
            
            // Log security event
            val securityEvent = when (newStatus) {
                AccountStatus.SUSPENDED, AccountStatus.BANNED -> SecurityEvent.ACCOUNT_SUSPENSION
                AccountStatus.ACTIVE -> SecurityEvent.ACCOUNT_REACTIVATION
                else -> SecurityEvent.ROLE_CHANGE
            }
            
            logSecurityEvent(
                targetUserId,
                securityEvent,
                mapOf(
                    "newStatus" to newStatus.name,
                    "updatedBy" to currentUserId,
                    "reason" to reason
                )
            )
            
            Log.d(TAG, "✅ Account status updated: $targetUserId -> ${newStatus.name}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update account status: ${e.message}")
            false
        }
    }
    
    /**
     * Update user profile information.
     */
    suspend fun updateUserProfile(
        userId: String,
        updates: Map<String, Any>
    ): Boolean {
        return try {
            // Users can only update their own profile, unless they're admin
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != userId && !hasPermission(Permission.MANAGE_USERS)) {
                Log.w(TAG, "User does not have permission to update this profile")
                return false
            }
            
            val sanitizedUpdates = updates.toMutableMap()
            sanitizedUpdates["updatedAt"] = System.currentTimeMillis()
            
            // Don't allow users to change their own role or account status
            if (currentUserId == userId) {
                sanitizedUpdates.remove("role")
                sanitizedUpdates.remove("accountStatus")
            }
            
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .update(sanitizedUpdates)
                .await()
            
            Log.d(TAG, "✅ Profile updated: $userId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update profile: ${e.message}")
            false
        }
    }
    
    /**
     * Log a security event for audit purposes.
     */
    suspend fun logSecurityEvent(
        userId: String,
        event: SecurityEvent,
        details: Map<String, Any> = emptyMap(),
        success: Boolean = true
    ) {
        try {
            val logEntry = SecurityAuditLog(
                userId = userId,
                event = event,
                details = details,
                success = success
            )
            
            firestore.collection(COLLECTION_SECURITY_LOGS)
                .add(logEntry)
                .await()
                
        } catch (e: Exception) {
            Log.w(TAG, "Failed to log security event: ${e.message}")
        }
    }
    
    /**
     * Get users by role (Admin/Moderator only).
     */
    suspend fun getUsersByRole(role: UserRole): List<UserProfile> {
        return try {
            if (!hasPermission(Permission.VIEW_ANALYTICS)) {
                return emptyList()
            }
            
            val snapshot = firestore.collection(COLLECTION_USERS)
                .whereEqualTo("role", role.name)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    UserProfile(
                        uid = doc.id,
                        email = data["email"] as? String ?: "",
                        displayName = data["displayName"] as? String ?: "",
                        role = role,
                        accountStatus = AccountStatus.fromString(data["accountStatus"] as? String),
                        emailVerified = data["emailVerified"] as? Boolean ?: false,
                        phoneVerified = data["phoneVerified"] as? Boolean ?: false,
                        twoFactorEnabled = data["twoFactorEnabled"] as? Boolean ?: false,
                        createdAt = data["createdAt"] as? Long ?: 0,
                        lastLoginAt = data["lastLoginAt"] as? Long ?: 0,
                        profileImageUrl = data["profileImageUrl"] as? String,
                        bio = data["bio"] as? String,
                        points = (data["points"] as? Long)?.toInt() ?: 0,
                        contributionCount = (data["contributionCount"] as? Long)?.toInt() ?: 0,
                        moderationScore = (data["moderationScore"] as? Double)?.toFloat() ?: 0.5f
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse user profile: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get users by role: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Initialize user profile on first login.
     */
    suspend fun initializeUserProfile(
        userId: String,
        email: String,
        displayName: String
    ): Boolean {
        return try {
            val initialRole = if (ADMIN_EMAILS.contains(email.lowercase())) {
                UserRole.ADMIN
            } else {
                UserRole.USER
            }
            val initialStatus = if (initialRole == UserRole.ADMIN) {
                AccountStatus.ACTIVE
            } else {
                AccountStatus.PENDING_VERIFICATION
            }
            val userProfile = mapOf(
                "email" to email,
                "displayName" to displayName,
                "role" to initialRole.name,
                "accountStatus" to initialStatus.name,
                "emailVerified" to false,
                "phoneVerified" to false,
                "twoFactorEnabled" to false,
                "createdAt" to System.currentTimeMillis(),
                "lastLoginAt" to System.currentTimeMillis(),
                "points" to 0,
                "contributionCount" to 0,
                "moderationScore" to 0.5f,
                "preferences" to mapOf(
                    "language" to "ar",
                    "theme" to "system",
                    "notificationsEnabled" to true,
                    "studyRemindersEnabled" to true,
                    "emailNotificationsEnabled" to true,
                    "soundEnabled" to true,
                    "vibrationEnabled" to true,
                    "autoPlayTTS" to false,
                    "dailyGoal" to 20,
                    "reminderTime" to "19:00"
                )
            )
            
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .set(userProfile)
                .await()
            
            logSecurityEvent(userId, SecurityEvent.LOGIN)
            
            Log.d(TAG, "✅ User profile initialized: $userId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize user profile: ${e.message}")
            false
        }
    }
    
    private fun parseUserPreferences(prefsMap: Map<String, Any>?): UserPreferences {
        if (prefsMap == null) return UserPreferences()
        
        return UserPreferences(
            language = prefsMap["language"] as? String ?: "ar",
            theme = prefsMap["theme"] as? String ?: "system",
            notificationsEnabled = prefsMap["notificationsEnabled"] as? Boolean ?: true,
            studyRemindersEnabled = prefsMap["studyRemindersEnabled"] as? Boolean ?: true,
            emailNotificationsEnabled = prefsMap["emailNotificationsEnabled"] as? Boolean ?: true,
            soundEnabled = prefsMap["soundEnabled"] as? Boolean ?: true,
            vibrationEnabled = prefsMap["vibrationEnabled"] as? Boolean ?: true,
            autoPlayTTS = prefsMap["autoPlayTTS"] as? Boolean ?: false,
            dailyGoal = (prefsMap["dailyGoal"] as? Long)?.toInt() ?: 20,
            reminderTime = prefsMap["reminderTime"] as? String ?: "19:00"
        )
    }
    
    private suspend fun logRoleAssignment(
        assignerId: String,
        targetUserId: String,
        oldRole: UserRole,
        newRole: UserRole,
        reason: String
    ) {
        try {
            firestore.collection(COLLECTION_ROLE_ASSIGNMENTS).add(mapOf(
                "assignerId" to assignerId,
                "targetUserId" to targetUserId,
                "oldRole" to oldRole.name,
                "newRole" to newRole.name,
                "reason" to reason,
                "timestamp" to System.currentTimeMillis()
            )).await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to log role assignment: ${e.message}")
        }
    }
}