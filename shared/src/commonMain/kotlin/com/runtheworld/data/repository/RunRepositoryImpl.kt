package com.runtheworld.data.repository

import com.runtheworld.data.local.db.RunDao
import com.runtheworld.data.local.db.toDomain
import com.runtheworld.data.local.db.toEntity
import com.runtheworld.domain.model.Run
import com.runtheworld.domain.repository.RunRepository
import com.runtheworld.util.AppResult
import com.runtheworld.util.appRunCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RunRepositoryImpl(private val dao: RunDao) : RunRepository {

    override fun observeRuns(): Flow<List<Run>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveRun(run: Run): AppResult<Unit> =
        appRunCatching { dao.insert(run.toEntity()) }

    override suspend fun deleteRun(id: String): AppResult<Unit> =
        appRunCatching { dao.deleteById(id) }

    override suspend fun getRunById(id: String): AppResult<Run> =
        appRunCatching {
            dao.getById(id)?.toDomain()
                ?: throw NoSuchElementException("Run $id not found")
        }
}
