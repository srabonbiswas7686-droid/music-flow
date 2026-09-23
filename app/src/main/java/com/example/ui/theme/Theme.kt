package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MusicFlowDarkColorScheme = darkColorScheme(
    primary = FlowCyan,
    onPrimary = Color.Black,
    primaryContainer = FlowViolet,
    onPrimaryContainer = Color.White,
    secondary = FlowVioletLight,
    onSecondary = Color.Black,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = FlowMagenta,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    error = FlowPink,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional music theme consistent across all Android versions
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MusicFlowDarkColorScheme,
        typography = Typography,
        content = content
    )
}
