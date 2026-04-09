package com.runtheworld.domain.model

data class Run(
    val id: String,
    val startedAt: Long,          // epoch millis
    val endedAt: Long,            // epoch millis
    val distanceMeters: Double,
    val areaKm2: Double,
    val path: List<GpsPoint>,     // raw GPS trace
    val claimedPolygon: List<GpsPoint>  // convex hull of the path
) {
    val durationSeconds: Long get() = (endedAt - startedAt) / 1000L
    val durationFormatted: String get() {
        val h = durationSeconds / 3600
        val m = (durationSeconds % 3600) / 60
        val s = durationSeconds % 60
        return if (h > 0)
            "${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        else
            "${m}:${s.toString().padStart(2, '0')}"
    }
    val distanceFormatted: String get() =
        if (distanceMeters >= 1000) {
            val v = kotlin.math.round(distanceMeters / 1000.0 * 100).toLong()
            "${v / 100}.${(v % 100).toString().padStart(2, '0')} km"
        } else {
            "${kotlin.math.round(distanceMeters).toLong()} m"
        }
}
