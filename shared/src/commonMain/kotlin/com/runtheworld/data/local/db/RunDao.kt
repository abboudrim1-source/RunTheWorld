package com.runtheworld.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {
    @Query("SELECT * FROM runs ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs WHERE id = :id")
    suspend fun getById(id: String): RunEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: RunEntity)

    @Query("DELETE FROM runs WHERE id = :id")
    suspend fun deleteById(id: String)
}
