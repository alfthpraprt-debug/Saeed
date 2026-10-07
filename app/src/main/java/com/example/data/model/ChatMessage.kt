package com.example.data.model

data class ChatMessage(
    val id: Long = 0,
    val sessionId: Long = 0,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoice: Boolean = false,
    val isSpeaking: Boolean = false
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}
