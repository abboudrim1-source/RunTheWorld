package com.runtheworld.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Android actual for the SceneViewGlobe expect.
 *
 * SceneView (Filament PBR) is in your Gradle — to use a real 3D GLB model:
 *   1. Place your file at  app/src/androidMain/assets/models/globe.glb
 *      (free sources: NASA 3D Resources, Sketchfab, or export from Blender)
 *   2. Swap this body for a Scene { ModelNode(...) } implementation.
 *
 * Until then the Canvas globe renders immediately with no extra assets.
 */
@Composable
actual fun SceneViewGlobe(modifier: Modifier) {
    AnimatedGlobe3D(modifier = modifier)
}
