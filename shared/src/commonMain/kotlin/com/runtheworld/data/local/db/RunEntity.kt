package com.runtheworld.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Run

@Entity(tableName = "runs")
@TypeConverters(Converters::class)
data class RunEntity(
    @PrimaryKey val id: String,
    val startedAt: Long,
    val endedAt: Long,
    val distanceMeters: Double,
    val areaKm2: Double,
    val path: List<GpsPoint>,
    val claimedPolygon: List<GpsPoint>
)

fun RunEntity.toDomain() = Run(
    id = id,
    startedAt = startedAt,
    endedAt = endedAt,
    distanceMeters = distanceMeters,
    areaKm2 = areaKm2,
    path = path,
    claimedPolygon = claimedPolygon
)

fun Run.toEntity() = RunEntity(
    id = id,
    startedAt = startedAt,
    endedAt = endedAt,
    distanceMeters = distanceMeters,
    areaKm2 = areaKm2,
    path = path,
    claimedPolygon = claimedPolygon
)
