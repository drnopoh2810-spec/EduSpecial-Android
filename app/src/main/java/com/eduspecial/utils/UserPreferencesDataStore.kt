package com.eduspecial.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "eduspecial_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        val KEY_LAST_SYNC = longPreferencesKey("last_sync_timestamp")
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_STUDY_NOTIFICATIONS = booleanPreferencesKey("study_notifications")
        val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val KEY_DAILY_GOAL = intPreferencesKey("daily_goal_cards")
        val KEY_REMINDER_TIME = longPreferencesKey("reminder_time_millis")
        val KEY_DISPLAY_NAME  = stringPreferencesKey("display_name")
        val KEY_AVATAR_URL    = stringPreferencesKey("avatar_url")
        val KEY_PERMISSIONS_DONE = booleanPreferencesKey("permissions_done")
    }

    val userId: Flow<String?> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_USER_ID] }

    val authToken: Flow<String?> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_AUTH_TOKEN] }

    val lastSyncTimestamp: Flow<Long> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_LAST_SYNC] ?: 0L }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_DARK_THEME] ?: false }

    val dailyGoal: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_DAILY_GOAL] ?: 10 }

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_ONBOARDING_DONE] ?: false }

    val reminderTimeMillis: Flow<Long> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_REMINDER_TIME] ?: (8 * 60 * 60 * 1000L) } // default 08:00

    val displayName: Flow<String?> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_DISPLAY_NAME] }

    val avatarUrl: Flow<String?> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_AVATAR_URL] }

    val isPermissionsDone: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_PERMISSIONS_DONE] ?: false }

    val studyNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_STUDY_NOTIFICATIONS] ?: true }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { it[KEY_AUTH_TOKEN] = token }
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { it[KEY_USER_ID] = userId }
    }

    suspend fun updateLastSync() {
        context.dataStore.edit { it[KEY_LAST_SYNC] = System.currentTimeMillis() }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_THEME] = enabled }
    }

    suspend fun setDailyGoal(goal: Int) {
        context.dataStore.edit { it[KEY_DAILY_GOAL] = goal }
    }

    suspend fun markOnboardingDone() {
        context.dataStore.edit { it[KEY_ONBOARDING_DONE] = true }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun setReminderTime(timeMillis: Long) {
        context.dataStore.edit { it[KEY_REMINDER_TIME] = timeMillis }
    }

    suspend fun setDisplayName(name: String) {
        context.dataStore.edit { it[KEY_DISPLAY_NAME] = name }
    }

    suspend fun setAvatarUrl(url: String) {
        context.dataStore.edit { it[KEY_AVATAR_URL] = url }
    }

    suspend fun setStudyNotifications(enabled: Boolean) {
        context.dataStore.edit { it[KEY_STUDY_NOTIFICATIONS] = enabled }
    }

    suspend fun markPermissionsDone() {
        context.dataStore.edit { it[KEY_PERMISSIONS_DONE] = true }
    }
}
