package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.JarvisScreen
import com.example.ui.viewmodel.JarvisViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                // Check initial mic permission
                LaunchedEffect(Unit) {
                    val hasMicPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                    viewModel.setMicPermission(hasMicPermission)
                }

                // Audio Permission Launcher
                val micPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    viewModel.setMicPermission(isGranted)
                    if (isGranted) {
                        viewModel.startListening()
                    }
                }

                // Speech Recognizer Intent Launcher Fallback
                val speechIntentLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val data = result.data
                    val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val text = results?.firstOrNull() ?: ""
                    if (text.isNotBlank()) {
                        viewModel.sendMessage(text, isVoice = true)
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground),
                    contentWindowInsets = WindowInsets.systemBars,
                    bottomBar = {
                        JarvisBottomNavigation(
                            currentScreen = uiState.currentScreen,
                            onNavigate = { screen -> viewModel.navigateTo(screen) }
                        )
                    }
                ) { innerPadding ->
                    val contentModifier = Modifier.padding(innerPadding)

                    when (uiState.currentScreen) {
                        JarvisScreen.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onRequestMicPermission = {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                modifier = contentModifier
                            )
                        }

                        JarvisScreen.CHAT -> {
                            ChatScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onRequestMicPermission = {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                modifier = contentModifier
                            )
                        }

                        JarvisScreen.VOICE -> {
                            VoiceScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onRequestMicPermission = {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                modifier = contentModifier
                            )
                        }

                        JarvisScreen.HISTORY -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                modifier = contentModifier
                            )
                        }

                        JarvisScreen.SETTINGS -> {
                            SettingsScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                modifier = contentModifier
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JarvisBottomNavigation(
    currentScreen: JarvisScreen,
    onNavigate: (JarvisScreen) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        modifier = Modifier.border(1.dp, GlassBorder)
    ) {
        val navItems = listOf(
            Triple(JarvisScreen.HOME, Icons.Default.Home, "Home"),
            Triple(JarvisScreen.CHAT, Icons.AutoMirrored.Filled.Chat, "Chat"),
            Triple(JarvisScreen.VOICE, Icons.Default.GraphicEq, "Voice"),
            Triple(JarvisScreen.HISTORY, Icons.Default.History, "History"),
            Triple(JarvisScreen.SETTINGS, Icons.Default.Settings, "Settings")
        )

        navItems.forEach { (screen, icon, label) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkBackground,
                    selectedTextColor = JarvisCyanBright,
                    indicatorColor = JarvisCyan,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextDim
                ),
                modifier = Modifier.testTag("nav_${label.lowercase()}")
            )
        }
    }
}
