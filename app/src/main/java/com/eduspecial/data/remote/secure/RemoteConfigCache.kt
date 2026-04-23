package com.eduspecial.data.remote.secure

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.runtimeConfigStore by preferencesDataStore(name = "runtime_config")

/**
 * Persists the last successfully fetched [RuntimeConfig] so the app can boot
 * offline after the first successful sync.
 */
@Singleton
class RemoteConfigCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "RemoteConfigCache"
        private val KEY_JSON       = stringPreferencesKey("config_json")
        private val KEY_FETCHED_AT = longPreferencesKey("fetched_at_ms")
    }

    private val gson = Gson()

    suspend fun save(config: RuntimeConfig) {
        context.runtimeConfigStore.edit {
            it[KEY_JSON]       = gson.toJson(config)
            it[KEY_FETCHED_AT] = System.currentTimeMillis()
        }
        Log.d(TAG, "💾 runtime config cached")
    }

    suspend fun load(): RuntimeConfig? {
        val prefs = context.runtimeConfigStore.data.first()
        val json  = prefs[KEY_JSON] ?: return null
        return try {
            gson.fromJson(json, RuntimeConfig::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "cached config corrupt — discarding")
            null
        }
    }

    suspend fun fetchedAtMs(): Long =
        context.runtimeConfigStore.data.first()[KEY_FETCHED_AT] ?: 0L

    suspend fun isFresh(ttlMs: Long = BootstrapConfig.CACHE_TTL_MS): Boolean =
        System.currentTimeMillis() - fetchedAtMs() < ttlMs
}
