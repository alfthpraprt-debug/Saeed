package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.AssistantState
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisRed
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun JarvisCoreView(
    state: AssistantState,
    rmsDb: Float = 0f,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "jarvis_core")

    // Slow continuous rotation
    val rotationClockwise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    AssistantState.THINKING -> 2000
                    AssistantState.SPEAKING -> 4000
                    AssistantState.LISTENING -> 3500
                    else -> 10000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_cw"
    )

    // Counter clockwise rotation for inner ring
    val rotationCounter by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    AssistantState.THINKING -> 1500
                    AssistantState.SPEAKING -> 3000
                    AssistantState.LISTENING -> 2800
                    else -> 8000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_ccw"
    )

    // Breathing pulse for core glow
    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    AssistantState.THINKING -> 700
                    AssistantState.LISTENING -> 600
                    AssistantState.SPEAKING -> 900
                    else -> 2400
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Primary accent color based on state
    val coreColor = when (state) {
        AssistantState.ERROR -> JarvisRed
        AssistantState.THINKING -> JarvisAmber
        AssistantState.LISTENING -> JarvisCyanBright
        AssistantState.SPEAKING -> JarvisElectricBlue
        AssistantState.IDLE -> JarvisCyan
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("jarvis_ai_core")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val maxRadius = this.size.minDimension / 2f

            // Dynamic audio expansion factor
            val audioScale = if (state == AssistantState.LISTENING) {
                1f + (rmsDb / 10f) * 0.35f
            } else if (state == AssistantState.SPEAKING) {
                breathingPulse
            } else {
                1f
            }

            // Outer subtle radar boundary ring
            drawCircle(
                color = coreColor.copy(alpha = 0.15f),
                radius = maxRadius * 0.96f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Outer segmented rotation ring
            rotate(rotationClockwise, pivot = center) {
                val outerRadius = maxRadius * 0.90f * audioScale
                // 4 segmented arcs
                for (i in 0 until 4) {
                    drawArc(
                        color = coreColor.copy(alpha = 0.55f),
                        startAngle = i * 90f + 15f,
                        sweepAngle = 60f,
                        useCenter = false,
                        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                        size = androidx.compose.ui.geometry.Size(outerRadius * 2, outerRadius * 2),
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Tech tick marks
                val tickCount = 24
                for (i in 0 until tickCount) {
                    val angleDeg = i * (360f / tickCount)
                    val rad = Math.toRadians(angleDeg.toDouble())
                    val r1 = maxRadius * 0.92f
                    val r2 = maxRadius * 0.95f
                    val start = Offset(center.x + (r1 * cos(rad)).toFloat(), center.y + (r1 * sin(rad)).toFloat())
                    val end = Offset(center.x + (r2 * cos(rad)).toFloat(), center.y + (r2 * sin(rad)).toFloat())
                    drawLine(
                        color = coreColor.copy(alpha = if (i % 3 == 0) 0.6f else 0.25f),
                        start = start,
                        end = end,
                        strokeWidth = if (i % 3 == 0) 2.dp.toPx() else 1.dp.toPx()
                    )
                }
            }

            // Middle counter-rotating energy ring
            rotate(rotationCounter, pivot = center) {
                val midRadius = maxRadius * 0.72f
                // 3 segmented arcs
                for (i in 0 until 3) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                coreColor.copy(alpha = 0.1f),
                                coreColor.copy(alpha = 0.85f),
                                JarvisCyanBright
                            ),
                            center = center
                        ),
                        startAngle = i * 120f + 10f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(center.x - midRadius, center.y - midRadius),
                        size = androidx.compose.ui.geometry.Size(midRadius * 2, midRadius * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Orbital node dots
                val nodeAngles = listOf(0.0, 120.0, 240.0)
                nodeAngles.forEach { deg ->
                    val rad = Math.toRadians(deg)
                    val dotPos = Offset(
                        center.x + (midRadius * cos(rad)).toFloat(),
                        center.y + (midRadius * sin(rad)).toFloat()
                    )
                    drawCircle(
                        color = JarvisCyanBright,
                        radius = 3.5.dp.toPx(),
                        center = dotPos
                    )
                }
            }

            // Inner glowing pulse circle
            val innerGlowRadius = maxRadius * 0.52f * breathingPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        coreColor.copy(alpha = 0.35f),
                        coreColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = innerGlowRadius * 1.4f
                ),
                radius = innerGlowRadius * 1.4f,
                center = center
            )

            // Inner arc reactor boundary ring
            drawCircle(
                color = coreColor.copy(alpha = 0.7f),
                radius = innerGlowRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Center radiant reactor core orb
            val centerOrbRadius = maxRadius * 0.32f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JarvisCyanBright,
                        coreColor,
                        JarvisElectricBlue.copy(alpha = 0.8f)
                    ),
                    center = center,
                    radius = centerOrbRadius
                ),
                radius = centerOrbRadius,
                center = center
            )

            // Center luminous bright highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = centerOrbRadius * 0.38f,
                center = center
            )
        }
    }
}
