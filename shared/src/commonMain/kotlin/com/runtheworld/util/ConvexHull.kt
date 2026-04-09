package com.runtheworld.util

import com.runtheworld.domain.model.GpsPoint
import kotlin.math.atan2

/**
 * Graham scan convex hull. Returns the minimal polygon enclosing all GPS points.
 * Returns an empty list if fewer than 3 points are provided.
 */
object ConvexHull {

    fun compute(points: List<GpsPoint>): List<GpsPoint> {
        if (points.size < 3) return points

        // Find the lowest (then leftmost) point as pivot
        val pivot = points.minWith(compareBy({ it.lat }, { it.lng }))

        // Sort by polar angle relative to pivot, then by distance
        val sorted = points
            .filter { it != pivot }
            .sortedWith(compareBy(
                { -atan2(it.lat - pivot.lat, it.lng - pivot.lng) },
                { -distSquared(pivot, it) }
            ))
            .toMutableList()
        sorted.add(0, pivot)

        val hull = ArrayDeque<GpsPoint>()
        for (point in sorted) {
            while (hull.size >= 2 && cross(hull[hull.size - 2], hull.last(), point) <= 0) {
                hull.removeLast()
            }
            hull.addLast(point)
        }
        return hull.toList()
    }

    /** Signed area of the parallelogram formed by vectors O->A and O->B. */
    private fun cross(o: GpsPoint, a: GpsPoint, b: GpsPoint): Double =
        (a.lng - o.lng) * (b.lat - o.lat) - (a.lat - o.lat) * (b.lng - o.lng)

    private fun distSquared(a: GpsPoint, b: GpsPoint): Double {
        val dLat = b.lat - a.lat
        val dLng = b.lng - a.lng
        return dLat * dLat + dLng * dLng
    }
}
