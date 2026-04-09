package com.runtheworld.data.repository

import com.runtheworld.data.local.db.TerritoryDao
import com.runtheworld.data.local.db.toDomain
import com.runtheworld.data.local.db.toEntity
import com.runtheworld.domain.model.Territory
import com.runtheworld.domain.repository.TerritoryRepository
import com.runtheworld.util.AppResult
import com.runtheworld.util.appRunCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TerritoryRepositoryImpl(private val dao: TerritoryDao) : TerritoryRepository {

    override fun observeTerritories(): Flow<List<Territory>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun claimTerritory(territory: Territory): AppResult<Unit> =
        appRunCatching { dao.insert(territory.toEntity()) }

    override suspend fun deleteTerritoriesOwnedBy(username: String): AppResult<Unit> =
        appRunCatching { dao.deleteByOwner(username) }
}
