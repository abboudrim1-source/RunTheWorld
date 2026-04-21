package com.runtheworld.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Run
import com.runtheworld.domain.model.Territory

@Composable
expect fun RunTheWorldMap(
    modifier: Modifier,
    territories: List<Territory>,
    currentUserUsername: String?,
    currentPath: List<GpsPoint>,
    userLocation: GpsPoint?,
    pastRuns: List<Run> = emptyList(),
    showMyLocationButton: Boolean = false,
    followUser: Boolean = false,
    recenterTrigger: Int = 0,
    userColorHex: String = "#1A73E8"
)
