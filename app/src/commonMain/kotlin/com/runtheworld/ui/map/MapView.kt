package com.runtheworld.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Territory

/**
 * Platform-specific full-screen map composable.
 *
 * Android actual → Google Maps Compose (maps-compose)
 * iOS actual     → MapKit wrapped in UIKitView
 */
@Composable
expect fun RunTheWorldMap(
    modifier: Modifier,
    territories: List<Territory>,
    currentUserUsername: String?,
    currentPath: List<GpsPoint>,
    userLocation: GpsPoint?
)
