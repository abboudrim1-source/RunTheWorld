package com.runtheworld.domain.repository

import com.runtheworld.domain.model.Territory
import com.runtheworld.util.AppResult
import kotlinx.coroutines.flow.Flow

interface TerritoryRepository {
    fun observeTerritories(): Flow<List<Territory>>
    suspend fun claimTerritory(territory: Territory): AppResult<Unit>
    suspend fun deleteTerritoriesOwnedBy(username: String): AppResult<Unit>
}
