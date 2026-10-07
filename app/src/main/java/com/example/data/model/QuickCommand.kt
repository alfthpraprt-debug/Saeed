package com.example.data.model

data class QuickCommand(
    val id: String,
    val title: String,
    val prompt: String,
    val iconName: String,
    val category: String
)

val defaultQuickCommands = listOf(
    QuickCommand(
        id = "weather",
        title = "Check Weather",
        prompt = "What is the weather forecast right now?",
        iconName = "cloud",
        category = "Utility"
    ),
    QuickCommand(
        id = "date",
        title = "Current Time & Date",
        prompt = "What is today's current date and time?",
        iconName = "schedule",
        category = "Utility"
    ),
    QuickCommand(
        id = "explain",
        title = "Explain Concept",
        prompt = "Explain quantum computing in simple, concise terms.",
        iconName = "psychology",
        category = "Knowledge"
    ),
    QuickCommand(
        id = "translate_urdu",
        title = "Translate into Urdu",
        prompt = "Translate this phrase into formal Urdu: 'Welcome to the future of artificial intelligence.'",
        iconName = "translate",
        category = "Language"
    ),
    QuickCommand(
        id = "write_message",
        title = "Draft Formal Email",
        prompt = "Draft a polite, professional follow-up email to a project manager.",
        iconName = "edit_note",
        category = "Productivity"
    ),
    QuickCommand(
        id = "youtube_title",
        title = "YouTube Video Titles",
        prompt = "Give me 5 viral, high-CTR YouTube video titles about artificial intelligence breakthroughs in 2026.",
        iconName = "smart_display",
        category = "Creative"
    ),
    QuickCommand(
        id = "work_assist",
        title = "Organize Work Tasks",
        prompt = "Help me organize a productive schedule for a high-priority software launch today.",
        iconName = "task_alt",
        category = "Productivity"
    ),
    QuickCommand(
        id = "reminder",
        title = "Set Reminder",
        prompt = "Help me outline the steps to set a daily reminder for health and code review.",
        iconName = "alarm",
        category = "Utility"
    )
)
