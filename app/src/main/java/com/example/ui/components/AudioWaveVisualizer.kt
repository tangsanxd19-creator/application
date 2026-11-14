package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan

@Composable
fun AudioWaveVisualizer(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 18,
    activeColor: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")

    val animValues = List(barCount) { index ->
        val duration = 380 + (index % 5) * 120
        infiniteTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = if (isSpeaking) 0.95f else 0.22f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            val totalWidth = size.width
            val totalHeight = size.height
            val barWidth = (totalWidth / (barCount * 1.8f)).coerceAtMost(10.dp.toPx())
            val spacing = (totalWidth - (barWidth * barCount)) / (barCount + 1)

            val gradient = Brush.verticalGradient(
                colors = listOf(CyberViolet, ElectricBlue, activeColor)
            )

            for (i in 0 until barCount) {
                val scale = if (isSpeaking) animValues[i].value else 0.15f
                val barHeight = (totalHeight * scale).coerceAtLeast(6.dp.toPx())
                val x = spacing + i * (barWidth + spacing)
                val y = (totalHeight - barHeight) / 2f

                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }
    }
}
