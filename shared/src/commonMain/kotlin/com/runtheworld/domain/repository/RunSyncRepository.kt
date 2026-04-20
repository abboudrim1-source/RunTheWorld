package com.runtheworld.domain.repository

import com.runtheworld.domain.model.Run
import com.runtheworld.util.AppResult

interface RunSyncRepository {
    suspend fun syncRun(run: Run): AppResult<Unit>
}
