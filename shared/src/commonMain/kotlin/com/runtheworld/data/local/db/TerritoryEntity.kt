package com.runtheworld.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Territory

@Entity(tableName = "territories")
@TypeConverters(Converters::class)
data class TerritoryEntity(
    @PrimaryKey val id: String,
    val ownerUsername: String,
    val ownerColorHex: String,
    val polygon: List<GpsPoint>,
    val claimedAt: Long,
    val areaKm2: Double
)

fun TerritoryEntity.toDomain() = Territory(
    id = id,
    ownerUsername = ownerUsername,
    ownerColorHex = ownerColorHex,
    polygon = polygon,
    claimedAt = claimedAt,
    areaKm2 = areaKm2
)

fun Territory.toEntity() = TerritoryEntity(
    id = id,
    ownerUsername = ownerUsername,
    ownerColorHex = ownerColorHex,
    polygon = polygon,
    claimedAt = claimedAt,
    areaKm2 = areaKm2
)
