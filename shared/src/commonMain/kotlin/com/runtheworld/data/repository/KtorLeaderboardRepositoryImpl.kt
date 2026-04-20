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
    val color: String
)

class KtorLeaderboardRepositoryImpl(
    private val httpClient: HttpClient
) : LeaderboardRepository {

    private val baseUrl = com.runtheworld.data.network.NetworkConfig.BASE_URL

    override suspend fun getLeaderboard(): AppResult<List<LeaderboardEntry>> = appRunCatching {
        val dtos = httpClient.get("$baseUrl/leaderboard").body<List<LeaderboardEntryDto>>()
        dtos.mapIndexed { index, dto ->
            LeaderboardEntry(
                rank = index + 1,
                username = dto.username,
                totalAreaKm2 = dto.area,
                runCount = dto.runs,
                colorHex = dto.color
            )
        }
    }
}
