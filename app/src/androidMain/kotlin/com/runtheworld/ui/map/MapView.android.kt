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
    userLocation: GpsPoint?,
    pastPaths: List<List<GpsPoint>>,
    showMyLocationButton: Boolean,
    userColorHex: String
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
        properties = MapProperties(isMyLocationEnabled = true),
        uiSettings = MapUiSettings(myLocationButtonEnabled = showMyLocationButton, zoomControlsEnabled = false)
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

        // Draw saved run paths — same style as active trace but slightly thinner
        val pastPathColor = parseHexColor(userColorHex).copy(alpha = 0.75f)
        pastPaths.forEach { path ->
            if (path.size > 1) {
                val pts = path.map { LatLng(it.lat, it.lng) }
                Polyline(points = pts, color = androidx.compose.ui.graphics.Color(0xFFFFFFFF), width = 16f, zIndex = 1f)
                Polyline(points = pts, color = pastPathColor, width = 10f, zIndex = 2f)
            }
        }

        // Draw the active GPS trace like Google Maps directions: white border + user color on top
        if (currentPath.size > 1) {
            val pathPoints = currentPath.map { LatLng(it.lat, it.lng) }
            val traceColor = parseHexColor(userColorHex)
            Polyline(
                points = pathPoints,
                color = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                width = 22f,
                zIndex = 2f
            )
            Polyline(
                points = pathPoints,
                color = traceColor,
                width = 16f,
                zIndex = 3f
            )
        }
    }
}
