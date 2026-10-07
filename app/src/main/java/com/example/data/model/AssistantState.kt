package com.example.data.model

enum class AssistantState(val label: String) {
    IDLE("Ready"),
    LISTENING("Listening..."),
    THINKING("Thinking..."),
    SPEAKING("Speaking..."),
    ERROR("Alert")
}
