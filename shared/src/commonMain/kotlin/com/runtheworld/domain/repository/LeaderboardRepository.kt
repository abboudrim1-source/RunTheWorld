package com.runtheworld.domain.repository

import com.runtheworld.domain.model.LeaderboardEntry
import com.runtheworld.util.AppResult

interface LeaderboardRepository {
    suspend fun getLeaderboard(): AppResult<List<LeaderboardEntry>>
}
