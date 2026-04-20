package com.runtheworld.data.repository

import com.runtheworld.domain.model.LeaderboardEntry
import com.runtheworld.domain.repository.LeaderboardRepository
import com.runtheworld.util.AppResult
import com.runtheworld.util.appRunCatching
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
private data class LeaderboardEntryDto(
    val username: String,
    val area: Double,
    val runs: Int,
    val color: String,
    val city: String? = null
)

class KtorLeaderboardRepositoryImpl(
    private val httpClient: HttpClient
) : LeaderboardRepository {

    private val baseUrl get() = com.runtheworld.data.network.NetworkConfig.BASE_URL

    override suspend fun getLeaderboard(city: String?): AppResult<List<LeaderboardEntry>> = appRunCatching {
        val url = if (city != null) "$baseUrl/leaderboard?city=$city" else "$baseUrl/leaderboard"
        val dtos = httpClient.get(url).body<List<LeaderboardEntryDto>>()
        dtos.mapIndexed { index, dto ->
            LeaderboardEntry(
                rank         = index + 1,
                username     = dto.username,
                totalAreaKm2 = dto.area,
                runCount     = dto.runs,
                colorHex     = dto.color,
                city         = dto.city
            )
        }
    }

    override suspend fun getCities(): List<String> = try {
        httpClient.get("$baseUrl/leaderboard/cities").body()
    } catch (_: Exception) { emptyList() }
}
