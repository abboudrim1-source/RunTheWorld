package com.runtheworld.domain.model

data class Territory(
    val id: String,
    val ownerUsername: String,
    val ownerColorHex: String,   // "#RRGGBB"
    val polygon: List<GpsPoint>, // convex hull vertices
    val claimedAt: Long,         // epoch millis
    val areaKm2: Double
) {
    val isOwnedByCurrentUser: Boolean get() = false  // resolved in ViewModel
}
