package com.example.data.repository

import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatSessionEntity
import com.example.data.model.ChatMessage
import com.example.data.model.MessageRole
import com.example.data.remote.GeminiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val chatDao: ChatDao,
    private val settingsRepository: SettingsRepository
) {
    val allSessions: Flow<List<ChatSessionEntity>> = chatDao.getAllSessions()

    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForSession(sessionId).map { list ->
            list.map { entity ->
                ChatMessage(
                    id = entity.id,
                    sessionId = entity.sessionId,
                    role = if (entity.role == "USER") MessageRole.USER else MessageRole.ASSISTANT,
                    content = entity.content,
                    timestamp = entity.timestamp,
                    isVoice = entity.isVoice
                )
            }
        }
    }

    suspend fun createNewSession(title: String = "New Transmission"): Long {
        val session = ChatSessionEntity(
            title = title,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            messageCount = 0
        )
        return chatDao.insertSession(session)
    }

    suspend fun addMessage(
        sessionId: Long,
        role: MessageRole,
        content: String,
        isVoice: Boolean = false
    ): Long {
        val roleStr = if (role == MessageRole.USER) "USER" else "ASSISTANT"
        val entity = ChatMessageEntity(
            sessionId = sessionId,
            role = roleStr,
            content = content,
            timestamp = System.currentTimeMillis(),
            isVoice = isVoice
        )
        val msgId = chatDao.insertMessage(entity)

        // Update session summary and timestamp
        val session = chatDao.getSessionById(sessionId)
        if (session != null) {
            val newTitle = if (session.messageCount == 0 && role == MessageRole.USER) {
                if (content.length > 36) content.take(33) + "..." else content
            } else {
                session.title
            }
            chatDao.updateSession(
                session.copy(
                    title = newTitle,
                    updatedAt = System.currentTimeMillis(),
                    messageCount = session.messageCount + 1
                )
            )
        }
        return msgId
    }

    suspend fun getGeminiConversationHistory(sessionId: Long, currentPrompt: String): List<GeminiMessage> {
        val isMemoryEnabled = settingsRepository.settings.value.isMemoryEnabled
        if (!isMemoryEnabled) {
            return listOf(GeminiMessage(role = "user", text = currentPrompt))
        }

        // Fetch recent messages
        val recentEntities = chatDao.getRecentMessages(sessionId, limit = 20)
        val history = mutableListOf<GeminiMessage>()

        recentEntities.forEach { entity ->
            val role = if (entity.role == "USER") "user" else "model"
            history.add(GeminiMessage(role = role, text = entity.content))
        }

        // Add the current prompt if not already present as the last message
        if (history.isEmpty() || history.last().text != currentPrompt) {
            history.add(GeminiMessage(role = "user", text = currentPrompt))
        }

        return history
    }

    suspend fun deleteSession(sessionId: Long) {
        chatDao.deleteSessionById(sessionId)
    }

    suspend fun clearAllHistory() {
        chatDao.deleteAllMessages()
        chatDao.deleteAllSessions()
    }
}
