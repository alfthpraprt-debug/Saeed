package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.AssistantState
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisElectricBlue
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    state: AssistantState,
    rmsDb: Float = 0f,
    modifier: Modifier = Modifier,
    barCount: Int = 28,
    height: Dp = 56.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val idlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "idle_phase"
    )

    val barBrush = Brush.verticalGradient(
        colors = when (state) {
            AssistantState.THINKING -> listOf(JarvisAmber, JarvisCyan)
            AssistantState.SPEAKING -> listOf(JarvisCyanBright, JarvisElectricBlue)
            AssistantState.LISTENING -> listOf(JarvisCyanBright, JarvisCyan)
            else -> listOf(JarvisCyan.copy(alpha = 0.6f), JarvisElectricBlue.copy(alpha = 0.3f))
        }
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .testTag("audio_waveform")
    ) {
        val width = size.width
        val canvasHeight = size.height
        val barWidth = (width / (barCount * 1.6f)).coerceAtLeast(2.dp.toPx())
        val spacing = (width - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

        val normalizedRms = (rmsDb / 10f).coerceIn(0f, 1f)

        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing)
            val centerOffset = (i - barCount / 2f) / (barCount / 2f)
            val bellFactor = (1f - centerOffset * centerOffset).coerceIn(0.2f, 1f)

            val rawHeightFraction = when (state) {
                AssistantState.SPEAKING -> {
                    val s1 = sin(phase + i * 0.4).toFloat()
                    val s2 = sin(phase * 1.5 + i * 0.7).toFloat()
                    val combined = ((s1 + s2) / 2f + 1f) / 2f // 0..1
                    (0.25f + combined * 0.75f) * bellFactor
                }
                AssistantState.LISTENING -> {
                    val flutter = (sin(phase * 2 + i * 0.5).toFloat() * 0.2f)
                    (0.15f + normalizedRms * 0.8f + flutter).coerceIn(0.1f, 1.0f) * bellFactor
                }
                AssistantState.THINKING -> {
                    val pulse = ((sin(phase * 3 + i * 0.3).toFloat() + 1f) / 2f)
                    (0.2f + pulse * 0.45f) * bellFactor
                }
                else -> {
                    // IDLE: very subtle ambient heartbeat
                    val idleS = ((sin(idlePhase + i * 0.25).toFloat() + 1f) / 2f)
                    (0.10f + idleS * 0.12f) * bellFactor
                }
            }

            val barHeight = (rawHeightFraction * canvasHeight).coerceIn(4.dp.toPx(), canvasHeight)
            val y = (canvasHeight - barHeight) / 2f

            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
