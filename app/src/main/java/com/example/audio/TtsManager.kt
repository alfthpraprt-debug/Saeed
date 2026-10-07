package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    var onSpeechStarted: (() -> Unit)? = null
    var onSpeechFinished: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.US
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    onSpeechStarted?.invoke()
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    onSpeechFinished?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    onSpeechFinished?.invoke()
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    onSpeechFinished?.invoke()
                }
            })
        } else {
            Log.e("TtsManager", "TextToSpeech initialization failed with status $status")
            isInitialized = false
        }
    }

    fun speak(
        text: String,
        speechSpeed: Float = 1.0f,
        speechPitch: Float = 1.0f,
        language: String = "Auto-Detect"
    ) {
        if (!isInitialized || tts == null) {
            Log.w("TtsManager", "TTS not initialized yet")
            return
        }

        stop()

        val cleanText = sanitizeTextForSpeech(text)
        if (cleanText.isBlank()) return

        try {
            // Apply language if specific
            val targetLocale = when (language) {
                "Urdu" -> Locale.forLanguageTag("ur-PK")
                "Hindi" -> Locale.forLanguageTag("hi-IN")
                "English" -> Locale.US
                else -> {
                    // Check if text has Urdu/Arabic script or Hindi Devanagari
                    if (containsUrduScript(cleanText)) {
                        Locale.forLanguageTag("ur-PK")
                    } else if (containsHindiScript(cleanText)) {
                        Locale.forLanguageTag("hi-IN")
                    } else {
                        Locale.US
                    }
                }
            }

            val availability = tts?.isLanguageAvailable(targetLocale) ?: -1
            if (availability >= TextToSpeech.LANG_AVAILABLE) {
                tts?.language = targetLocale
            } else {
                tts?.language = Locale.US
            }

            tts?.setSpeechRate(speechSpeed.coerceIn(0.5f, 2.0f))
            tts?.setPitch(speechPitch.coerceIn(0.5f, 2.0f))

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "jarvis_utterance_${System.currentTimeMillis()}")
            }

            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "jarvis_speech_id")
        } catch (e: Exception) {
            Log.e("TtsManager", "Error in speak: ${e.message}", e)
            _isSpeaking.value = false
        }
    }

    fun stop() {
        try {
            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
        } catch (e: Exception) {
            Log.w("TtsManager", "Error stopping TTS: ${e.message}")
        } finally {
            _isSpeaking.value = false
            onSpeechFinished?.invoke()
        }
    }

    fun shutdown() {
        try {
            stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.w("TtsManager", "Error shutting down TTS: ${e.message}")
        }
    }

    private fun sanitizeTextForSpeech(input: String): String {
        return input
            // Remove markdown formatting
            .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
            .replace(Regex("\\*([^*]+)\\*"), "$1")
            .replace(Regex("`{1,3}[^`]+`{1,3}"), "")
            .replace(Regex("#+\\s*"), "")
            .replace(Regex("\\[([^\\]]+)\\]\\([^)]+\\)"), "$1")
            // Remove bullet point symbols
            .replace("•", "")
            .replace("—", ", ")
            // Remove emojis or special symbols that sound awkward in TTS
            .replace(Regex("[\\p{So}\\p{Cn}]"), "")
            .trim()
    }

    private fun containsUrduScript(text: String): Boolean {
        return text.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' }
    }

    private fun containsHindiScript(text: String): Boolean {
        return text.any { it in '\u0900'..'\u097F' }
    }
}
