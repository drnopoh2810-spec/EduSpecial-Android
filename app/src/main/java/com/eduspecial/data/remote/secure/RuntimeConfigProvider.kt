package com.eduspecial.data.remote.secure

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory holder for the active [RuntimeConfig].
 *
 * Bootstrap order on app start:
 *   1) Try to load from [RemoteConfigCache] (offline fallback).
 *   2) If the cache is stale (or missing) fetch from the secure channel and update.
 *
 * Anything in the app that needs Cloudinary/Algolia/etc. reads from this
 * holder — there are no hard-coded defaults anywhere in the codebase.
 */
@Singleton
class RuntimeConfigProvider @Inject constructor(
    private val client: RemoteConfigClient,
    private val cache:  RemoteConfigCache
) {
    companion object { private const val TAG = "RuntimeConfigProvider" }

    private val _config = MutableStateFlow<RuntimeConfig?>(null)
    val config: StateFlow<RuntimeConfig?> = _config.asStateFlow()

    val current: RuntimeConfig?
        get() = _config.value

    /**
     * Loads cache → optionally refreshes from network.
     * Returns true if a usable config is now available.
     */
    suspend fun bootstrap(): Boolean {
        // 1) Hydrate from cache
        cache.load()?.let { _config.value = it; Log.d(TAG, "📦 loaded cached runtime config") }

        // 2) Refresh if stale or missing
        if (_config.value == null || !cache.isFresh()) {
            client.fetch().onSuccess { fresh ->
                _config.value = fresh
                cache.save(fresh)
            }.onFailure { e ->
                Log.w(TAG, "⚠️ refresh failed, sticking to cache: ${e.message}")
            }
        }

        return _config.value != null
    }

    /** Force a refresh (e.g. user pulled to refresh in settings). */
    suspend fun refresh(): Boolean {
        val r = client.fetch()
        return r.onSuccess { _config.value = it; cache.save(it) }.isSuccess
    }
}
