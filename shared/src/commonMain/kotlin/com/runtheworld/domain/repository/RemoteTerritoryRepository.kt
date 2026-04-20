package com.runtheworld.domain.repository

import com.runtheworld.domain.model.Territory

interface RemoteTerritoryRepository {
    suspend fun syncTerritory(territory: Territory)
    suspend fun fetchForUsers(uids: Set<String>): List<Territory>
}
