package com.runtheworld.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// iOS has no SceneView; fall back to the shared Canvas globe.
@Composable
actual fun SceneViewGlobe(modifier: Modifier) {
    AnimatedGlobe3D(modifier = modifier)
}
