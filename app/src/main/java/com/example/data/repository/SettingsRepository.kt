package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class JarvisSettings(
    val assistantName: String = "J.A.R.V.I.S.",
    val userTitle: String = "Sir",
    val speechSpeed: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val language: String = "Auto-Detect",
    val isMemoryEnabled: Boolean = true,
    val isSoundFeedbackEnabled: Boolean = true,
    val customApiKey: String = ""
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jarvis_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<JarvisSettings> = _settings.asStateFlow()

    private fun loadSettings(): JarvisSettings {
        return JarvisSettings(
            assistantName = prefs.getString("assistant_name", "J.A.R.V.I.S.") ?: "J.A.R.V.I.S.",
            userTitle = prefs.getString("user_title", "Sir") ?: "Sir",
            speechSpeed = prefs.getFloat("speech_speed", 1.0f),
            speechPitch = prefs.getFloat("speech_pitch", 1.0f),
            language = prefs.getString("language", "Auto-Detect") ?: "Auto-Detect",
            isMemoryEnabled = prefs.getBoolean("memory_enabled", true),
            isSoundFeedbackEnabled = prefs.getBoolean("sound_feedback", true),
            customApiKey = prefs.getString("custom_api_key", "") ?: ""
        )
    }

    fun updateAssistantName(name: String) {
        prefs.edit().putString("assistant_name", name).apply()
        _settings.value = _settings.value.copy(assistantName = name)
    }

    fun updateUserTitle(title: String) {
        prefs.edit().putString("user_title", title).apply()
        _settings.value = _settings.value.copy(userTitle = title)
    }

    fun updateSpeechSpeed(speed: Float) {
        prefs.edit().putFloat("speech_speed", speed).apply()
        _settings.value = _settings.value.copy(speechSpeed = speed)
    }

    fun updateSpeechPitch(pitch: Float) {
        prefs.edit().putFloat("speech_pitch", pitch).apply()
        _settings.value = _settings.value.copy(speechPitch = pitch)
    }

    fun updateLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
        _settings.value = _settings.value.copy(language = language)
    }

    fun setMemoryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("memory_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isMemoryEnabled = enabled)
    }

    fun setSoundFeedbackEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_feedback", enabled).apply()
        _settings.value = _settings.value.copy(isSoundFeedbackEnabled = enabled)
    }

    fun updateCustomApiKey(key: String) {
        prefs.edit().putString("custom_api_key", key).apply()
        _settings.value = _settings.value.copy(customApiKey = key)
    }
}
