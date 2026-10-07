package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssistantState
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
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.MicGlowColor
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.JarvisScreen
import com.example.ui.viewmodel.JarvisUiState
import com.example.ui.viewmodel.JarvisViewModel

@Composable
fun VoiceScreen(
    viewModel: JarvisViewModel,
    uiState: JarvisUiState,
    onRequestMicPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(JarvisScreen.HOME)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top HUD Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(JarvisScreen.HOME) },
                modifier = Modifier.testTag("voice_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = JarvisCyan
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NEURAL VOICE CONSOLE",
                    color = JarvisCyanBright,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "FULL DUPLEX ACOUSTIC TELEMETRY",
                    color = TextDim,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(
                onClick = { viewModel.navigateTo(JarvisScreen.CHAT) },
                modifier = Modifier.testTag("voice_to_chat_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Open Chat",
                    tint = JarvisCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Giant JARVIS Arc Core
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            JarvisCoreView(
                state = uiState.assistantState,
                rmsDb = uiState.rmsDb,
                size = 260.dp,
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

        // Status Feedback
        Text(
            text = when (uiState.assistantState) {
                AssistantState.LISTENING -> "LISTENING TO TRANSMISSION..."
                AssistantState.THINKING -> "NEURAL SYNAPSE ENGAGED..."
                AssistantState.SPEAKING -> "TRANSMITTING ACOUSTIC SYNTHESIS..."
                AssistantState.ERROR -> "SYSTEM ALERT: ${uiState.errorMessage ?: "TRY AGAIN"}"
                AssistantState.IDLE -> "READY FOR COMMAND, ${uiState.settings.userTitle.uppercase()}"
            },
            color = when (uiState.assistantState) {
                AssistantState.ERROR -> JarvisRed
                AssistantState.THINKING -> JarvisAmber
                AssistantState.LISTENING -> JarvisCyanBright
                AssistantState.SPEAKING -> JarvisElectricBlue
                AssistantState.IDLE -> JarvisCyan
            },
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Live Audio Waveform
        WaveformVisualizer(
            state = uiState.assistantState,
            rmsDb = uiState.rmsDb,
            height = 52.dp,
            barCount = 32
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live Speech Transcript Box
        AnimatedVisibility(
            visible = uiState.liveTranscript.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column {
                    Text(
                        text = "INPUT RECOGNIZED:",
                        color = JarvisCyan,
                        fontSize = 10.sp,
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

        // Active Voice Response Box
        AnimatedVisibility(
            visible = uiState.activeAiResponse.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.settings.assistantName.uppercase()} VOCALIZATION",
                            color = JarvisCyanBright,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            if (uiState.isSpeaking) {
                                IconButton(
                                    onClick = { viewModel.stopSpeaking() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop",
                                        tint = JarvisRed
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { viewModel.speakResponse(uiState.activeAiResponse) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Replay Speech",
                                        tint = JarvisCyan
                                    )
                                }
                            }
                            IconButton(
                                onClick = { viewModel.copyToClipboard(uiState.activeAiResponse) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
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

        Spacer(modifier = Modifier.height(20.dp))

        // Giant Mic Control and Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stop button (left)
            IconButton(
                onClick = { viewModel.stopSpeaking() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isSpeaking) JarvisRed.copy(alpha = 0.2f) else DarkSurfaceVariant)
                    .border(1.dp, if (uiState.isSpeaking) JarvisRed else GlassBorder, CircleShape)
                    .testTag("voice_stop_speaking_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop Speaking",
                    tint = if (uiState.isSpeaking) JarvisRed else TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Primary Mic Button (center)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (uiState.isListening) JarvisCyanBright else JarvisCyan,
                                if (uiState.isListening) MicGlowColor else JarvisElectricBlue.copy(alpha = 0.35f),
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
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isListening) JarvisCyanBright else JarvisCyan)
                        .testTag("voice_screen_mic_button")
                ) {
                    Icon(
                        imageVector = if (uiState.isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = DarkBackground,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Re-read or Test Speech (right)
            IconButton(
                onClick = {
                    if (uiState.activeAiResponse.isNotBlank()) {
                        viewModel.speakResponse(uiState.activeAiResponse)
                    } else {
                        viewModel.testVoiceSpeech()
                    }
                },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .border(1.dp, GlassBorder, CircleShape)
                    .testTag("voice_read_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Read Aloud",
                    tint = JarvisCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
