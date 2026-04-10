package com.runtheworld.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = darkColorScheme(
    primary          = Color(0xFFFF5733),
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF3A1200),
    secondary        = Color(0xFFFF1F8E),
    onSecondary      = Color.White,
    tertiary         = Color(0xFF00D4FF),
    onTertiary       = Color.Black,
    background       = Color(0xFF050510),
    onBackground     = Color.White,
    surface          = Color(0xFF0D0D2B),
    onSurface        = Color.White,
    surfaceVariant   = Color(0xFF1A1A3E),
    onSurfaceVariant = Color(0xCCFFFFFF),
    error            = Color(0xFFFF4466),
    onError          = Color.White,
    outline          = Color(0x44FFFFFF)
)

@Composable
fun RunTheWorldTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
