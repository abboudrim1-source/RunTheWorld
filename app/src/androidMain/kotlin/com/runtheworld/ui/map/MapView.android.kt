package com.runtheworld.ui.map

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Territory
import com.runtheworld.ui.theme.parseHexColor
import androidx.compose.ui.graphics.toArgb

@Composable
actual fun RunTheWorldMap(
    modifier: Modifier,
    territories: List<Territory>,
    currentUserUsername: String?,
    currentPath: List<GpsPoint>,
    userLocation: GpsPoint?
) {
    val defaultPosition = LatLng(48.8566, 2.3522)  // Paris fallback
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation?.let { LatLng(it.lat, it.lng) } ?: defaultPosition,
            14f
        )
    }

    // Follow the user when a new location arrives
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(it.lat, it.lng), 15f)
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false),
        uiSettings = MapUiSettings(myLocationButtonEnabled = false)
    ) {
        // Draw all claimed territories
        territories.forEach { territory ->
            val isOwn = territory.ownerUsername == currentUserUsername
            val fillColor = parseHexColor(territory.ownerColorHex).copy(alpha = if (isOwn) 0.45f else 0.25f)
            val strokeColor = parseHexColor(territory.ownerColorHex).copy(alpha = if (isOwn) 0.9f else 0.6f)

            Polygon(
                points = territory.polygon.map { LatLng(it.lat, it.lng) },
                fillColor = fillColor,
                strokeColor = strokeColor,
                strokeWidth = if (isOwn) 4f else 2f,
                zIndex = if (isOwn) 1f else 0f
            )
        }

        // Draw the active GPS trace as a polyline
        if (currentPath.size > 1) {
            Polyline(
                points = currentPath.map { LatLng(it.lat, it.lng) },
                color = androidx.compose.ui.graphics.Color(0xFF33AFFF),
                width = 10f,
                zIndex = 2f
            )
        }

        // Current location dot
        userLocation?.let {
            Circle(
                center = LatLng(it.lat, it.lng),
                radius = 8.0,
                fillColor = androidx.compose.ui.graphics.Color(0xFF33AFFF),
                strokeColor = androidx.compose.ui.graphics.Color.White,
                strokeWidth = 2f,
                zIndex = 3f
            )
        }
    }
}
