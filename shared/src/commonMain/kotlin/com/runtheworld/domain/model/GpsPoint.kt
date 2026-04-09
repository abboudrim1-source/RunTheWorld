package com.runtheworld.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GpsPoint(
    val lat: Double,
    val lng: Double,
    val timestamp: Long = 0L  // epoch millis
)
