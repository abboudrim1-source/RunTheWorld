package com.runtheworld.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val rtw_orange = Color(0xFFFF5733)
private val rtw_dark = Color(0xFF1A1A2E)
private val rtw_surface = Color(0xFF16213E)
private val rtw_accent = Color(0xFF0F3460)

private val DarkColorScheme = darkColorScheme(
    primary = rtw_orange,
    onPrimary = Color.White,
    secondary = rtw_accent,
    onSecondary = Color.White,
    background = rtw_dark,
    surface = rtw_surface,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = rtw_orange,
    onPrimary = Color.White,
    secondary = rtw_accent,
    onSecondary = Color.White,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onBackground = Color(0xFF1A1A2E),
    onSurface = Color(0xFF1A1A2E),
    error = Color(0xFFB00020)
)

@Composable
fun RunTheWorldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
