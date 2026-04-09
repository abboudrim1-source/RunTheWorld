package com.runtheworld.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Parse an RGB hex string ("#RRGGBB" or "#AARRGGBB") without using android.graphics.Color.
 * Works in commonMain (CMP).
 */
fun parseHexColor(hex: String): Color {
    val cleaned = hex.trimStart('#')
    return when (cleaned.length) {
        6 -> {
            val r = cleaned.substring(0, 2).toInt(16)
            val g = cleaned.substring(2, 4).toInt(16)
            val b = cleaned.substring(4, 6).toInt(16)
            Color(r, g, b)
        }
        8 -> {
            val a = cleaned.substring(0, 2).toInt(16)
            val r = cleaned.substring(2, 4).toInt(16)
            val g = cleaned.substring(4, 6).toInt(16)
            val b = cleaned.substring(6, 8).toInt(16)
            Color(r, g, b, a)
        }
        else -> Color.Gray
    }
}
