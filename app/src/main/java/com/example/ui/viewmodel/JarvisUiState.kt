package com.example.ui.viewmodel

import com.example.data.local.entity.ChatSessionEntity
import com.example.data.model.AssistantState
import com.example.data.model.ChatMessage
import com.example.data.repository.JarvisSettings

enum class JarvisScreen {
    HOME,
    CHAT,
    VOICE,
    HISTORY,
    SETTINGS
}

data class JarvisUiState(
    val currentScreen: JarvisScreen = JarvisScreen.HOME,
    val assistantState: AssistantState = AssistantState.IDLE,
    val currentSessionId: Long = 0L,
    val currentSessionTitle: String = "Main Command Terminal",
    val messages: List<ChatMessage> = emptyList(),
    val allSessions: List<ChatSessionEntity> = emptyList(),
    val activeAiResponse: String = "",
    val liveTranscript: String = "",
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val rmsDb: Float = 0f,
    val errorMessage: String? = null,
    val settings: JarvisSettings = JarvisSettings(),
    val micPermissionGranted: Boolean = false
)
