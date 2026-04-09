package com.runtheworld.data.local.db

import androidx.room.TypeConverter
import com.runtheworld.domain.model.GpsPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

class Converters {
    @TypeConverter
    fun fromGpsPointList(value: List<GpsPoint>): String = json.encodeToString(value)

    @TypeConverter
    fun toGpsPointList(value: String): List<GpsPoint> = json.decodeFromString(value)
}
