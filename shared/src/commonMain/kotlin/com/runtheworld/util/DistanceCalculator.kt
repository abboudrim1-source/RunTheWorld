package com.runtheworld.util

import com.runtheworld.domain.model.GpsPoint
import kotlin.math.*

/**
 * Haversine formula — distance between two GPS coordinates in metres.
 * Uses kotlin.math only (no java.lang.Math) — compiles on iOS/Native.
 */
object DistanceCalculator {

    private const val EARTH_RADIUS_M = 6_371_000.0

    private fun toRadians(degrees: Double): Double = degrees * PI / 180.0

    fun distanceBetween(a: GpsPoint, b: GpsPoint): Double {
        val lat1 = toRadians(a.lat)
        val lat2 = toRadians(b.lat)
        val dLat = toRadians(b.lat - a.lat)
        val dLng = toRadians(b.lng - a.lng)

        val h = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLng / 2).pow(2)
        return 2 * EARTH_RADIUS_M * asin(sqrt(h))
    }

    /** Total path length in metres. */
    fun totalDistance(path: List<GpsPoint>): Double {
        if (path.size < 2) return 0.0
        return path.zipWithNext().sumOf { (a, b) -> distanceBetween(a, b) }
    }

    /**
     * Approximate area of a convex polygon (Shoelace formula) in km².
     * Vertices must be ordered (CW or CCW) in lat/lng degrees.
     */
    fun polygonAreaKm2(polygon: List<GpsPoint>): Double {
        if (polygon.size < 3) return 0.0
        val n = polygon.size

        var degreeShoelace = 0.0
        for (i in 0 until n) {
            val j = (i + 1) % n
            degreeShoelace += polygon[i].lng * polygon[j].lat - polygon[j].lng * polygon[i].lat
        }
        degreeShoelace = abs(degreeShoelace) / 2.0

        val midLat = toRadians(polygon.map { it.lat }.average())
        val metersPerDegreeLat = 111_320.0
        val metersPerDegreeLng = 111_320.0 * cos(midLat)

        val areaM2 = degreeShoelace * metersPerDegreeLat * metersPerDegreeLng
        return areaM2 / 1_000_000.0
    }
}
