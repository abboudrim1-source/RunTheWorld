package com.runtheworld.domain.repository

import com.runtheworld.domain.model.LeaderboardEntry
import com.runtheworld.util.AppResult

interface LeaderboardRepository {
    suspend fun getLeaderboard(city: String? = null): AppResult<List<LeaderboardEntry>>
    suspend fun getCities(): List<String>
}
