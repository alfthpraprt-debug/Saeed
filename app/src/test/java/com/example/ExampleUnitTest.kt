package com.example

import com.example.data.model.AssistantState
import com.example.data.model.defaultQuickCommands
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun quickCommands_areDefined() {
        assertTrue(defaultQuickCommands.isNotEmpty())
        assertEquals(8, defaultQuickCommands.size)
        assertTrue(defaultQuickCommands.any { it.id == "weather" })
        assertTrue(defaultQuickCommands.any { it.id == "translate_urdu" })
    }

    @Test
    fun assistantState_hasCorrectLabels() {
        assertEquals("Ready", AssistantState.IDLE.label)
        assertEquals("Listening...", AssistantState.LISTENING.label)
        assertEquals("Thinking...", AssistantState.THINKING.label)
        assertEquals("Speaking...", AssistantState.SPEAKING.label)
    }
}
