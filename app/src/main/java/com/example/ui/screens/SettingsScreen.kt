package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.JarvisScreen
import com.example.ui.viewmodel.JarvisUiState
import com.example.ui.viewmodel.JarvisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: JarvisViewModel,
    uiState: JarvisUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(JarvisScreen.HOME)
    }

    val scrollState = rememberScrollState()

    var assistantNameInput by remember(uiState.settings.assistantName) { mutableStateOf(uiState.settings.assistantName) }
    var userTitleInput by remember(uiState.settings.userTitle) { mutableStateOf(uiState.settings.userTitle) }
    var customApiKeyInput by remember(uiState.settings.customApiKey) { mutableStateOf(uiState.settings.customApiKey) }
    var isApiKeySaved by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "SYSTEM CONFIGURATION",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { viewModel.navigateTo(JarvisScreen.HOME) },
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = JarvisCyan
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Hero Graphic Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.jarvis_hud_hero_1791362980544),
                    contentDescription = "JARVIS HUD Interface",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground.copy(alpha = 0.45f))
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            text = "J.A.R.V.I.S. NEURAL MATRIX",
                            color = JarvisCyanBright,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Sub-system status: Nominal // Model: gemini-3.5-flash",
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 1. Assistant & User Identity
            SettingsSectionHeader(title = "IDENTITY PROTOCOLS", icon = Icons.Default.Person)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Assistant Name
                    OutlinedTextField(
                        value = assistantNameInput,
                        onValueChange = {
                            assistantNameInput = it
                            if (it.isNotBlank()) viewModel.updateAssistantName(it.trim())
                        },
                        label = { Text("Assistant Call-sign", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("assistant_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    // User Title
                    OutlinedTextField(
                        value = userTitleInput,
                        onValueChange = {
                            userTitleInput = it
                            if (it.isNotBlank()) viewModel.updateUserTitle(it.trim())
                        },
                        label = { Text("User Honorific (e.g. Sir, Boss)", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("user_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    // Quick User Title Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Sir", "Boss", "Captain", "Doctor").forEach { titleOption ->
                            val isSelected = uiState.settings.userTitle.equals(titleOption, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) JarvisCyan.copy(alpha = 0.2f) else DarkSurfaceVariant)
                                    .border(1.dp, if (isSelected) JarvisCyan else GlassBorder, RoundedCornerShape(12.dp))
                                    .clickable {
                                        userTitleInput = titleOption
                                        viewModel.updateUserTitle(titleOption)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = titleOption,
                                    color = if (isSelected) JarvisCyanBright else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 2. Voice & Speech Synthesis
            SettingsSectionHeader(title = "ACOUSTIC & SYNTHESIS", icon = Icons.AutoMirrored.Filled.VolumeUp)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Test Speech Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Voice Output Diagnostic", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Test current synthesis rate & pitch", color = TextSecondary, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.testVoiceSpeech() },
                            colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                            modifier = Modifier.testTag("test_voice_button")
                        ) {
                            Text("Test Voice", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Speech Speed
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Speech Velocity", color = TextPrimary, fontSize = 13.sp)
                            Text(
                                text = String.format("%.2fx", uiState.settings.speechSpeed),
                                color = JarvisCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Slider(
                            value = uiState.settings.speechSpeed,
                            onValueChange = { viewModel.updateSpeechSpeed(it) },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = JarvisCyan,
                                activeTrackColor = JarvisCyan,
                                inactiveTrackColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.testTag("speech_speed_slider")
                        )
                    }

                    // Speech Pitch
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Frequency Pitch", color = TextPrimary, fontSize = 13.sp)
                            Text(
                                text = String.format("%.2fx", uiState.settings.speechPitch),
                                color = JarvisCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Slider(
                            value = uiState.settings.speechPitch,
                            onValueChange = { viewModel.updateSpeechPitch(it) },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = JarvisCyan,
                                activeTrackColor = JarvisCyan,
                                inactiveTrackColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.testTag("speech_pitch_slider")
                        )
                    }

                    // Language Selector
                    Column {
                        Text("Language Parsing Directive", color = TextPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Auto-Detect", "English", "Urdu", "Hindi", "Roman Urdu").forEach { lang ->
                                val isSelected = uiState.settings.language == lang
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) JarvisCyan.copy(alpha = 0.2f) else DarkSurfaceVariant)
                                        .border(1.dp, if (isSelected) JarvisCyan else GlassBorder, RoundedCornerShape(10.dp))
                                        .clickable { viewModel.updateLanguage(lang) }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = lang,
                                        color = if (isSelected) JarvisCyanBright else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Memory & Local Persistence
            SettingsSectionHeader(title = "NEURAL MEMORY BANKS", icon = Icons.Default.Memory)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Active Context Memory", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Remembers ongoing conversation turns across the active session.", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = uiState.settings.isMemoryEnabled,
                            onCheckedChange = { viewModel.setMemoryEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkBackground,
                                checkedTrackColor = JarvisCyan,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.testTag("memory_toggle")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Purge Chat History", color = TextPrimary, fontSize = 14.sp)
                            Text("Erase all stored sessions from Room database.", color = TextSecondary, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.clearAllHistory() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JarvisRed),
                            modifier = Modifier.testTag("clear_history_settings_button")
                        ) {
                            Text("Purge", color = JarvisRed)
                        }
                    }
                }
            }

            // 4. API & Model Configuration
            SettingsSectionHeader(title = "GEMINI INTELLIGENCE MATRIX", icon = Icons.Default.Key)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(JarvisGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ENGINE: gemini-3.5-flash // AUTONOMOUS MATRIX READY",
                            color = JarvisCyanBright,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "The app utilizes Gemini 3.5 Flash via AI Studio Secret Management. You can also specify an optional custom API key override below.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = customApiKeyInput,
                        onValueChange = {
                            customApiKeyInput = it
                            isApiKeySaved = false
                        },
                        placeholder = { Text("Optional Gemini API Key Override", color = TextDim, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_api_key_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (uiState.settings.customApiKey.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    customApiKeyInput = ""
                                    viewModel.updateCustomApiKey("")
                                    isApiKeySaved = false
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Reset Key", color = TextSecondary)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.updateCustomApiKey(customApiKeyInput.trim())
                                isApiKeySaved = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                            modifier = Modifier.testTag("save_api_key_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isApiKeySaved) "Saved" else "Save Key", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 5. About
            SettingsSectionHeader(title = "ABOUT J.A.R.V.I.S.", icon = Icons.Default.Info)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "J.A.R.V.I.S. AI PERSONAL ASSISTANT",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Version 2.4.0 (Build 2026.10)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Designed as an advanced, respectful, and intelligent voice-first personal assistant inspired by J.A.R.V.I.S. Supports real-time voice transcription, natural text-to-speech feedback, Urdu/Roman Urdu/English/Hindi comprehension, and persistent session memory.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = JarvisCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = JarvisCyanBright,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
