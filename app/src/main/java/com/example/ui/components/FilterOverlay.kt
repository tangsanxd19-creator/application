package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.data.model.VideoFilterType

@Composable
fun FilterOverlay(
    filter: VideoFilterType,
    modifier: Modifier = Modifier,
    intensity: Float = 1.0f
) {
    val alphaScale = intensity.coerceIn(0f, 1f)
    if (filter == VideoFilterType.NONE || alphaScale <= 0.01f) return

    Canvas(modifier = modifier.fillMaxSize()) {
        when (filter) {
            VideoFilterType.CYBER_GLOW -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x338B5CF6).copy(alpha = 0.20f * alphaScale),
                            Color(0x6606B6D4).copy(alpha = 0.36f * alphaScale)
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.45f),
                        radius = size.maxDimension * 0.7f
                    ),
                    blendMode = BlendMode.Screen
                )
            }
            VideoFilterType.STUDIO_LIGHT -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x28FFFFFF).copy(alpha = 0.16f * alphaScale),
                            Color.Transparent,
                            Color(0x44000000).copy(alpha = 0.26f * alphaScale)
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.35f),
                        radius = size.maxDimension * 0.8f
                    ),
                    blendMode = BlendMode.Overlay
                )
            }
            VideoFilterType.WARM_SUNSET -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x28F59E0B).copy(alpha = 0.18f * alphaScale),
                            Color(0x18EC4899).copy(alpha = 0.10f * alphaScale),
                            Color(0x353B82F6).copy(alpha = 0.18f * alphaScale)
                        )
                    ),
                    blendMode = BlendMode.Color
                )
            }
            VideoFilterType.VIRTUAL_BLUR -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x44080911).copy(alpha = 0.28f * alphaScale),
                            Color(0xAA080911).copy(alpha = 0.65f * alphaScale)
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.5f),
                        radius = size.maxDimension * 0.6f
                    )
                )
            }
            VideoFilterType.BEAUTY_SMOOTH -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x1CF472B6).copy(alpha = 0.12f * alphaScale),
                            Color(0x10DDD6FE).copy(alpha = 0.08f * alphaScale),
                            Color(0x22000000).copy(alpha = 0.12f * alphaScale)
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.4f),
                        radius = size.maxDimension * 0.65f
                    ),
                    blendMode = BlendMode.Screen
                )
            }
            VideoFilterType.VIVID_HDR -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x18FFFFFF).copy(alpha = 0.10f * alphaScale),
                            Color.Transparent,
                            Color(0x40080911).copy(alpha = 0.25f * alphaScale)
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.5f),
                        radius = size.maxDimension * 0.75f
                    ),
                    blendMode = BlendMode.Overlay
                )
            }
            VideoFilterType.NOIR_MONO -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x33000000).copy(alpha = 0.20f * alphaScale),
                            Color(0x88000000).copy(alpha = 0.52f * alphaScale)
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.5f),
                        radius = size.maxDimension * 0.68f
                    )
                )
            }
            VideoFilterType.NONE -> Unit
        }
    }
}
