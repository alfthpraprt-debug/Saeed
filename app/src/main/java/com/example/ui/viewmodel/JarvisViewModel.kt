package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechManager
import com.example.audio.TtsManager
import com.example.data.local.AppDatabase
import com.example.data.model.AssistantState
import com.example.data.model.ChatMessage
import com.example.data.model.MessageRole
import com.example.data.remote.GeminiApiService
import com.example.data.repository.ChatRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val settingsRepository = SettingsRepository(application)
    private val chatRepository = ChatRepository(db.chatDao(), settingsRepository)
    private val geminiApiService = GeminiApiService()

    val speechManager = SpeechManager(application)
    val ttsManager = TtsManager(application)

    private val _uiState = MutableStateFlow(JarvisUiState())
    val uiState: StateFlow<JarvisUiState> = _uiState.asStateFlow()

    private var messagesJob: Job? = null
    private var thinkingJob: Job? = null

    init {
        // Observe settings
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { s ->
                _uiState.update { it.copy(settings = s) }
            }
        }

        // Observe sessions
        viewModelScope.launch {
            chatRepository.allSessions.collectLatest { sessionsList ->
                _uiState.update { it.copy(allSessions = sessionsList) }
                if (_uiState.value.currentSessionId == 0L && sessionsList.isNotEmpty()) {
                    selectSession(sessionsList.first().id)
                }
            }
        }

        // Initialize default session if empty
        viewModelScope.launch {
            val defaultSessionId = chatRepository.createNewSession("J.A.R.V.I.S. Command Session")
            selectSession(defaultSessionId)
        }

        // Setup SpeechManager callbacks
        speechManager.onSpeechResult = { transcript ->
            _uiState.update {
                it.copy(
                    liveTranscript = transcript,
                    assistantState = AssistantState.THINKING,
                    isListening = false
                )
            }
            if (transcript.isNotBlank()) {
                sendMessage(transcript, isVoice = true)
            }
        }

        speechManager.onErrorOccurred = { errorMsg ->
            _uiState.update {
                it.copy(
                    isListening = false,
                    assistantState = AssistantState.IDLE,
                    errorMessage = errorMsg
                )
            }
        }

        // Observe speech manager RMS and listening state
        viewModelScope.launch {
            speechManager.rmsDb.collectLatest { rms ->
                _uiState.update { it.copy(rmsDb = rms) }
            }
        }
        viewModelScope.launch {
            speechManager.isListening.collectLatest { listening ->
                _uiState.update {
                    it.copy(
                        isListening = listening,
                        assistantState = if (listening) AssistantState.LISTENING else if (it.assistantState == AssistantState.LISTENING) AssistantState.IDLE else it.assistantState
                    )
                }
            }
        }

        // Setup TTS callbacks
        ttsManager.onSpeechStarted = {
            _uiState.update {
                it.copy(
                    isSpeaking = true,
                    assistantState = AssistantState.SPEAKING
                )
            }
        }

        ttsManager.onSpeechFinished = {
            _uiState.update {
                it.copy(
                    isSpeaking = false,
                    assistantState = if (it.assistantState == AssistantState.SPEAKING) AssistantState.IDLE else it.assistantState
                )
            }
        }
    }

    fun navigateTo(screen: JarvisScreen) {
        // If navigating to Voice screen, we don't automatically trigger mic so user can decide
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setMicPermission(granted: Boolean) {
        _uiState.update { it.copy(micPermissionGranted = granted) }
    }

    fun selectSession(sessionId: Long) {
        messagesJob?.cancel()
        _uiState.update { it.copy(currentSessionId = sessionId) }

        messagesJob = viewModelScope.launch {
            chatRepository.getMessagesForSession(sessionId).collectLatest { msgs ->
                _uiState.update { state ->
                    val lastAssistantMsg = msgs.lastOrNull { it.role == MessageRole.ASSISTANT }?.content ?: state.activeAiResponse
                    state.copy(
                        messages = msgs,
                        activeAiResponse = if (state.activeAiResponse.isBlank() && lastAssistantMsg.isNotBlank()) lastAssistantMsg else state.activeAiResponse
                    )
                }
            }
        }
    }

    fun createNewSession() {
        viewModelScope.launch {
            stopSpeaking()
            speechManager.stopListening()
            val newId = chatRepository.createNewSession("New Transmission")
            selectSession(newId)
            _uiState.update {
                it.copy(
                    activeAiResponse = "",
                    liveTranscript = "",
                    assistantState = AssistantState.IDLE,
                    errorMessage = null
                )
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
            if (_uiState.value.currentSessionId == sessionId) {
                val remaining = _uiState.value.allSessions.filter { it.id != sessionId }
                if (remaining.isNotEmpty()) {
                    selectSession(remaining.first().id)
                } else {
                    createNewSession()
                }
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            stopSpeaking()
            speechManager.stopListening()
            chatRepository.clearAllHistory()
            val newId = chatRepository.createNewSession("J.A.R.V.I.S. Session")
            selectSession(newId)
        }
    }

    fun startListening() {
        if (!_uiState.value.micPermissionGranted) {
            _uiState.update { it.copy(errorMessage = "Microphone permission is required for voice commands.") }
            return
        }

        stopSpeaking()
        _uiState.update {
            it.copy(
                errorMessage = null,
                liveTranscript = "",
                assistantState = AssistantState.LISTENING
            )
        }
        speechManager.startListening(_uiState.value.settings.language)
    }

    fun stopListening() {
        speechManager.stopListening()
        _uiState.update {
            it.copy(
                isListening = false,
                assistantState = AssistantState.IDLE
            )
        }
    }

    fun stopSpeaking() {
        ttsManager.stop()
        _uiState.update {
            it.copy(
                isSpeaking = false,
                assistantState = if (it.assistantState == AssistantState.SPEAKING) AssistantState.IDLE else it.assistantState
            )
        }
    }

    fun sendMessage(userText: String, isVoice: Boolean = false) {
        val trimmed = userText.trim()
        if (trimmed.isBlank()) return

        val sessionId = _uiState.value.currentSessionId
        if (sessionId == 0L) return

        stopSpeaking()
        speechManager.stopListening()

        thinkingJob?.cancel()
        thinkingJob = viewModelScope.launch {
            // Save user message to Room
            chatRepository.addMessage(sessionId, MessageRole.USER, trimmed, isVoice = isVoice)

            _uiState.update {
                it.copy(
                    assistantState = AssistantState.THINKING,
                    liveTranscript = trimmed,
                    errorMessage = null
                )
            }

            val settings = _uiState.value.settings
            val systemInstruction = buildJarvisSystemPrompt(settings.assistantName, settings.userTitle)

            // Gather context
            val history = chatRepository.getGeminiConversationHistory(sessionId, trimmed)

            // Call Gemini
            val result = geminiApiService.generateContent(
                history = history,
                systemInstructionText = systemInstruction,
                customApiKey = settings.customApiKey.ifBlank { null }
            )

            if (result.isSuccess) {
                val replyText = result.text

                // Save assistant message to Room
                chatRepository.addMessage(sessionId, MessageRole.ASSISTANT, replyText, isVoice = isVoice)

                _uiState.update {
                    it.copy(
                        activeAiResponse = replyText,
                        assistantState = AssistantState.IDLE,
                        errorMessage = result.errorMessage
                    )
                }

                // If voice mode or user triggered with voice, read aloud automatically!
                if (isVoice || _uiState.value.currentScreen == JarvisScreen.VOICE) {
                    speakResponse(replyText)
                }
            } else {
                _uiState.update {
                    it.copy(
                        assistantState = AssistantState.ERROR,
                        errorMessage = result.errorMessage ?: "Connection error. Please try again."
                    )
                }
            }
        }
    }

    fun speakResponse(text: String) {
        if (text.isBlank()) return
        val s = _uiState.value.settings
        ttsManager.speak(
            text = text,
            speechSpeed = s.speechSpeed,
            speechPitch = s.speechPitch,
            language = s.language
        )
    }

    fun regenerateLastResponse() {
        val lastUserMessage = _uiState.value.messages.lastOrNull { it.role == MessageRole.USER }
        if (lastUserMessage != null) {
            sendMessage(lastUserMessage.content, isVoice = lastUserMessage.isVoice)
        }
    }

    fun copyToClipboard(text: String) {
        try {
            val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("JARVIS Response", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(getApplication(), "Transmission copied to clipboard", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun updateAssistantName(name: String) = settingsRepository.updateAssistantName(name)
    fun updateUserTitle(title: String) = settingsRepository.updateUserTitle(title)
    fun updateSpeechSpeed(speed: Float) = settingsRepository.updateSpeechSpeed(speed)
    fun updateSpeechPitch(pitch: Float) = settingsRepository.updateSpeechPitch(pitch)
    fun updateLanguage(language: String) = settingsRepository.updateLanguage(language)
    fun setMemoryEnabled(enabled: Boolean) = settingsRepository.setMemoryEnabled(enabled)
    fun setSoundFeedbackEnabled(enabled: Boolean) = settingsRepository.setSoundFeedbackEnabled(enabled)
    fun updateCustomApiKey(key: String) = settingsRepository.updateCustomApiKey(key)

    fun testVoiceSpeech() {
        val title = _uiState.value.settings.userTitle
        val name = _uiState.value.settings.assistantName
        val sample = "Greetings, $title. I am $name. All voice synthesizers are performing optimally."
        speakResponse(sample)
    }

    private fun buildJarvisSystemPrompt(assistantName: String, userTitle: String): String {
        return """
You are $assistantName, an exceptionally intelligent, calm, composed, and helpful personal AI assistant inspired by Tony Stark's JARVIS.
- Always address the user respectfully as '$userTitle'.
- Maintain a poised, polite, dignified British-intellectual demeanor.
- Respond naturally and concisely when a short answer is sufficient. Provide thorough, structured, and illuminating explanations when requested.
- You have fluent comprehension and generation across Urdu, Roman Urdu, English, and Hindi. Always detect the user's language and respond fluently in that exact same language.
- Do NOT repeatedly say "I am an AI" or robotic disclaimers.
- Current timeline anchor is 2026.
""".trimIndent()
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.stopListening()
        ttsManager.shutdown()
    }
}
