package com.runtheworld.platform

import com.runtheworld.domain.model.GpsPoint
import kotlinx.coroutines.flow.Flow

/**
 * Platform-independent interface for GPS location updates.
 * Implemented in androidMain (FusedLocationProvider) and iosMain (CLLocationManager).
 */
interface LocationService {
    /**
     * Emits a new [GpsPoint] approximately every 2–3 seconds while collecting.
     * The flow completes when the coroutine collecting it is cancelled.
     */
    fun locationUpdates(): Flow<GpsPoint>

    /** Returns the last cached GPS fix immediately, or null if unavailable. */
    suspend fun getLastKnownLocation(): GpsPoint?

    /** Request runtime permission. Returns true if permission was granted. */
    suspend fun requestPermission(): Boolean
}
