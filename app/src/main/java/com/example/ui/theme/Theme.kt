package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AiConnectColorScheme = darkColorScheme(
    primary = CyberViolet,
    onPrimary = Color.White,
    primaryContainer = CyberVioletDark,
    onPrimaryContainer = PureWhite,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = ElectricBlueDark,
    onSecondaryContainer = PureWhite,
    tertiary = NeonCyan,
    onTertiary = ObsidianDark,
    background = ObsidianDark,
    onBackground = PureWhite,
    surface = ObsidianSurface,
    onSurface = PureWhite,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = SlateGray,
    outline = ObsidianCardBorder,
    error = CallDangerRed,
    onError = Color.White
)

@Composable
fun AiConnectTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AiConnectColorScheme,
        typography = Typography,
        content = content
    )
}
