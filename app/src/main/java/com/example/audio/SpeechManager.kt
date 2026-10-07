package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var onSpeechResult: ((String) -> Unit)? = null
    var onErrorOccurred: ((String) -> Unit)? = null

    val isRecognitionAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening(languageCode: String = "Auto-Detect") {
        if (!isRecognitionAvailable) {
            _errorMessage.value = "Speech recognition service unavailable on this device."
            onErrorOccurred?.invoke("Speech recognition service unavailable.")
            return
        }

        try {
            stopListening()

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        _errorMessage.value = null
                        _recognizedText.value = ""
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize roughly 0 to 10
                        _rmsDb.value = rmsdB.coerceIn(0f, 10f)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _rmsDb.value = 0f
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _rmsDb.value = 0f
                        val msg = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                            SpeechRecognizer.ERROR_NETWORK -> "Network connection error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timed out"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service busy"
                            SpeechRecognizer.ERROR_SERVER -> "Speech server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                            else -> "Voice recognition encountered an issue"
                        }
                        Log.w("SpeechManager", "Speech error code: $error: $msg")
                        _errorMessage.value = msg
                        onErrorOccurred?.invoke(msg)
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _rmsDb.value = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _recognizedText.value = text
                            onSpeechResult?.invoke(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _recognizedText.value = text
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = createSpeechIntent(languageCode)
            speechRecognizer?.startListening(intent)
            _isListening.value = true

        } catch (e: Exception) {
            Log.e("SpeechManager", "Failed to start speech recognizer: ${e.message}", e)
            _isListening.value = false
            _errorMessage.value = "Unable to start voice input: ${e.localizedMessage}"
            onErrorOccurred?.invoke(_errorMessage.value ?: "")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.w("SpeechManager", "Error stopping speech recognizer: ${e.message}")
        } finally {
            _isListening.value = false
            _rmsDb.value = 0f
        }
    }

    fun createSpeechIntent(languageCode: String = "Auto-Detect"): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

            val tag = when (languageCode) {
                "Urdu" -> "ur"
                "Hindi" -> "hi"
                "English" -> "en-US"
                else -> Locale.getDefault().toLanguageTag()
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, tag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, tag)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening for command, Sir...")
        }
        return intent
    }
}
