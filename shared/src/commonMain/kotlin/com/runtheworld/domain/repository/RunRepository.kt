package com.runtheworld.domain.repository

import com.runtheworld.domain.model.Run
import com.runtheworld.util.AppResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun observeRuns(): Flow<List<Run>>
    suspend fun saveRun(run: Run): AppResult<Unit>
    suspend fun deleteRun(id: String): AppResult<Unit>
    suspend fun getRunById(id: String): AppResult<Run>
}
