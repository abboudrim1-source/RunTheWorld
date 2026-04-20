package com.runtheworld.data.repository

import com.runtheworld.data.network.NetworkConfig
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Territory
import com.runtheworld.domain.repository.RemoteTerritoryRepository
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable private data class PolygonPoint(val lat: Double, val lng: Double)

@Serializable private data class TerritoryDto(
    val id: String,
    val userId: String,
    val ownerUsername: String,
    val ownerColorHex: String,
    val polygon: List<PolygonPoint>,
    val claimedAt: Long,
    val areaKm2: Double
)

class KtorRemoteTerritoryRepositoryImpl(
    private val httpClient: HttpClient,
    private val settings: Settings
) : RemoteTerritoryRepository {

    private val baseUrl get() = NetworkConfig.BASE_URL

    override suspend fun syncTerritory(territory: Territory) {
        val uid = settings.getStringOrNull("auth_uid") ?: return
        try {
            httpClient.post("$baseUrl/territories") {
                contentType(ContentType.Application.Json)
                setBody(TerritoryDto(
                    id            = territory.id,
                    userId        = uid,
                    ownerUsername = territory.ownerUsername,
                    ownerColorHex = territory.ownerColorHex,
                    polygon       = territory.polygon.map { PolygonPoint(it.lat, it.lng) },
                    claimedAt     = territory.claimedAt,
                    areaKm2       = territory.areaKm2
                ))
            }
        } catch (_: Exception) {}
    }

    override suspend fun fetchForUsers(uids: Set<String>): List<Territory> {
        if (uids.isEmpty()) return emptyList()
        return try {
            val response = httpClient.get("$baseUrl/territories/friends?uids=${uids.joinToString(",")}")
            if (!response.status.isSuccess()) return emptyList()
            response.body<List<TerritoryDto>>().map { dto ->
                Territory(
                    id            = dto.id,
                    ownerUsername = dto.ownerUsername,
                    ownerColorHex = dto.ownerColorHex,
                    polygon       = dto.polygon.map { GpsPoint(lat = it.lat, lng = it.lng) },
                    claimedAt     = dto.claimedAt,
                    areaKm2       = dto.areaKm2
                )
            }
        } catch (_: Exception) { emptyList() }
    }
}
