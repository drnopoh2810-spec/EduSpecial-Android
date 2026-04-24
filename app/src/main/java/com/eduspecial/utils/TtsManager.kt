package com.eduspecial.utils

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized Text-to-Speech manager using Android's built-in TTS engine.
 *
 * WHY BUILT-IN TTS:
 * - 100% free, no API key, no usage limits, no internet required
 * - Google TTS engine (pre-installed on all Android devices) uses neural voices
 *   that produce near-perfect English pronunciation — ideal for ABA/special-ed terms
 * - Works offline — critical for a learning app
 * - Zero latency — speech starts instantly, no network round-trip
 *
 * QUALITY:
 * - Uses Locale.US for American English pronunciation (standard for ABA terminology)
 * - Pitch and speed tuned for clear, educational speech
 * - Falls back gracefully if TTS engine is not available
 */
@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    enum class TtsState { INITIALIZING, READY, SPEAKING, ERROR, UNAVAILABLE }

    private val _state = MutableStateFlow(TtsState.INITIALIZING)
    val state: StateFlow<TtsState> = _state.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    // Tuned for clear educational pronunciation
    private val SPEECH_RATE  = 0.85f  // Slightly slower than default for clarity
    private val SPEECH_PITCH = 1.0f   // Natural pitch

    init {
        initialize()
    }

    private fun initialize() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to default locale
                    tts?.setLanguage(Locale.getDefault())
                    Log.w("TtsManager", "US English not available, using device default")
                }
                tts?.setSpeechRate(SPEECH_RATE)
                tts?.setPitch(SPEECH_PITCH)

                // Listen for utterance completion
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _state.value = TtsState.SPEAKING
                    }
                    override fun onDone(utteranceId: String?) {
                        _state.value = TtsState.READY
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _state.value = TtsState.READY
                    }
                    override fun onError(utteranceId: String?, errorCode: Int) {
                        Log.w("TtsManager", "TTS error code: $errorCode")
                        _state.value = TtsState.READY
                    }
                })

                isInitialized = true
                _state.value = TtsState.READY
                Log.d("TtsManager", "TTS initialized successfully")
            } else {
                _state.value = TtsState.UNAVAILABLE
                Log.e("TtsManager", "TTS initialization failed with status: $status")
            }
        }
    }

    /**
     * Speaks the given text using the device's TTS engine.
     *
     * @param text The text to speak (English term or definition)
     * @param locale Override locale — use Locale.US for English terms, Locale("ar") for Arabic
     * @param flushQueue If true, stops any current speech and speaks immediately
     */
    fun speak(
        text: String,
        locale: Locale = Locale.US,
        flushQueue: Boolean = true
    ) {
        if (!isInitialized || tts == null) {
            Log.w("TtsManager", "TTS not ready, ignoring speak request")
            return
        }

        val cleanText = text.trim()
        if (cleanText.isBlank()) return

        // Switch language if needed
        tts?.setLanguage(locale)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "edu_tts_${System.currentTimeMillis()}")
        }

        val queueMode = if (flushQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD

        tts?.speak(cleanText, queueMode, params, params.getString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))
    }

    /**
     * Speaks an English term with perfect US English pronunciation.
     * Used for the front face of flashcards (the term).
     */
    fun speakTerm(term: String) {
        speak(term, Locale.US, flushQueue = true)
    }

    /**
     * Speaks a definition — detects if it's Arabic or English and uses the right locale.
     * ABA definitions are typically in Arabic in this app.
     */
    fun speakDefinition(definition: String) {
        val locale = if (isArabicText(definition)) Locale("ar") else Locale.US
        speak(definition, locale, flushQueue = true)
    }

    /**
     * Stops any ongoing speech immediately.
     */
    fun stop() {
        if (isInitialized) {
            tts?.stop()
            _state.value = TtsState.READY
        }
    }

    val isSpeaking: Boolean
        get() = tts?.isSpeaking == true

    val isReady: Boolean
        get() = isInitialized && _state.value != TtsState.UNAVAILABLE

    /**
     * Releases TTS resources. Call when the app is destroyed.
     */
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        _state.value = TtsState.UNAVAILABLE
    }

    /**
     * Simple heuristic to detect Arabic text by checking for Arabic Unicode range.
     */
    private fun isArabicText(text: String): Boolean {
        val arabicChars = text.count { it.code in 0x0600..0x06FF }
        return arabicChars > text.length * 0.3
    }
}
