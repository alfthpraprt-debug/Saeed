package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssistantState
import com.example.data.model.defaultQuickCommands
import com.example.ui.components.GlassCard
import com.example.ui.components.JarvisCoreView
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisCyanDim
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.MicGlowColor
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.JarvisScreen
import com.example.ui.viewmodel.JarvisUiState
import com.example.ui.viewmodel.JarvisViewModel
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: JarvisViewModel,
    uiState: JarvisUiState,
    onRequestMicPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Determine respectful temporal greeting
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top HUD Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Protocol indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (uiState.assistantState) {
                                AssistantState.ERROR -> JarvisRed
                                AssistantState.THINKING -> JarvisAmber
                                AssistantState.LISTENING -> JarvisCyanBright
                                AssistantState.SPEAKING -> JarvisElectricBlue
                                AssistantState.IDLE -> JarvisGreen
                            }
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SYSTEM // " + uiState.assistantState.label.uppercase(),
                    color = JarvisCyanBright,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Timeline HUD
            Text(
                text = "OCTOBER 2026",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Greeting Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "$greeting, ${uiState.settings.userTitle}.",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "All systems operational. How may I assist you?",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center JARVIS AI Core
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            JarvisCoreView(
                state = uiState.assistantState,
                rmsDb = uiState.rmsDb,
                size = 230.dp,
                onClick = {
                    if (uiState.isSpeaking) {
                        viewModel.stopSpeaking()
                    } else if (uiState.isListening) {
                        viewModel.stopListening()
                    } else {
                        if (!uiState.micPermissionGranted) {
                            onRequestMicPermission()
                        } else {
                            viewModel.startListening()
                        }
                    }
                }
            )
        }

        // Status Label & Live Waveform
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = when (uiState.assistantState) {
                    AssistantState.LISTENING -> "Listening to your voice..."
                    AssistantState.THINKING -> "Processing neural transmission..."
                    AssistantState.SPEAKING -> "Synthesizing voice response..."
                    AssistantState.ERROR -> uiState.errorMessage ?: "Alert condition detected"
                    AssistantState.IDLE -> "Tap core or microphone to speak"
                },
                color = when (uiState.assistantState) {
                    AssistantState.ERROR -> JarvisRed
                    AssistantState.THINKING -> JarvisAmber
                    AssistantState.LISTENING -> JarvisCyanBright
                    AssistantState.SPEAKING -> JarvisElectricBlue
                    AssistantState.IDLE -> TextSecondary
                },
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            WaveformVisualizer(
                state = uiState.assistantState,
                rmsDb = uiState.rmsDb,
                height = 36.dp,
                barCount = 28
            )
        }

        // Live Transcript or Error Alert
        AnimatedVisibility(
            visible = uiState.liveTranscript.isNotBlank() && uiState.assistantState != AssistantState.IDLE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = "LIVE SPEECH INPUT",
                        color = JarvisCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${uiState.liveTranscript}\"",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Error Banner
        AnimatedVisibility(
            visible = uiState.errorMessage != null && uiState.assistantState == AssistantState.ERROR,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GlassCard(
                borderColor = JarvisRed.copy(alpha = 0.5f),
                backgroundColor = DarkSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NOTICE",
                            color = JarvisRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.errorMessage ?: "Connection error. Please try again.",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Active AI Response Card (if available)
        AnimatedVisibility(
            visible = uiState.activeAiResponse.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("home_response_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.settings.assistantName.uppercase() + " RESPONSE",
                            color = JarvisCyanBright,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            if (uiState.isSpeaking) {
                                IconButton(
                                    onClick = { viewModel.stopSpeaking() },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("stop_speech_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop Speech",
                                        tint = JarvisRed
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { viewModel.speakResponse(uiState.activeAiResponse) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("play_speech_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Read Aloud",
                                        tint = JarvisCyan
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.copyToClipboard(uiState.activeAiResponse) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("copy_response_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Response",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = uiState.activeAiResponse,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Command Directives
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COMMAND PROTOCOLS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "TAP TO RUN",
                color = JarvisCyanDim,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            defaultQuickCommands.forEach { cmd ->
                QuickCommandChip(
                    title = cmd.title,
                    icon = getCommandIcon(cmd.iconName),
                    onClick = {
                        viewModel.sendMessage(cmd.prompt, isVoice = false)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large Primary Voice & Chat Action Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chat button
            IconButton(
                onClick = { viewModel.navigateTo(JarvisScreen.CHAT) },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .border(1.dp, GlassBorder, CircleShape)
                    .testTag("open_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Open Chat",
                    tint = JarvisCyan
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Massive Glowing Voice Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (uiState.isListening) JarvisCyanBright else JarvisCyan,
                                if (uiState.isListening) MicGlowColor else JarvisElectricBlue.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                IconButton(
                    onClick = {
                        if (uiState.isListening) {
                            viewModel.stopListening()
                        } else {
                            if (!uiState.micPermissionGranted) {
                                onRequestMicPermission()
                            } else {
                                viewModel.startListening()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isListening) JarvisCyanBright else JarvisCyan)
                        .testTag("main_mic_button")
                ) {
                    Icon(
                        imageVector = if (uiState.isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = DarkBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Stop Speaking / Voice HUD Button
            IconButton(
                onClick = {
                    if (uiState.isSpeaking) {
                        viewModel.stopSpeaking()
                    } else {
                        viewModel.navigateTo(JarvisScreen.VOICE)
                    }
                },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isSpeaking) JarvisRed.copy(alpha = 0.2f) else DarkSurfaceVariant)
                    .border(1.dp, if (uiState.isSpeaking) JarvisRed else GlassBorder, CircleShape)
                    .testTag("stop_or_voice_screen_button")
            ) {
                Icon(
                    imageVector = if (uiState.isSpeaking) Icons.Default.Stop else Icons.Default.GraphicEq,
                    contentDescription = if (uiState.isSpeaking) "Stop Speaking" else "Voice HUD",
                    tint = if (uiState.isSpeaking) JarvisRed else JarvisCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun QuickCommandChip(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("quick_command_${title.lowercase().replace(" ", "_")}"),
        verticalAlignment = Alignment.CenterVertically
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
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getCommandIcon(name: String): ImageVector {
    return when (name) {
        "cloud" -> Icons.Default.Cloud
        "schedule" -> Icons.Default.Schedule
        "psychology" -> Icons.Default.Psychology
        "translate" -> Icons.Default.Translate
        "edit_note" -> Icons.Default.EditNote
        "smart_display" -> Icons.Default.SmartDisplay
        "task_alt" -> Icons.Default.TaskAlt
        "alarm" -> Icons.Default.Alarm
        else -> Icons.Default.GraphicEq
    }
}
