package com.example.data.remote

data class GeminiMessage(
    val role: String, // "user" or "model"
    val text: String
)

data class GeminiGenerationResult(
    val isSuccess: Boolean,
    val text: String,
    val errorMessage: String? = null
)
