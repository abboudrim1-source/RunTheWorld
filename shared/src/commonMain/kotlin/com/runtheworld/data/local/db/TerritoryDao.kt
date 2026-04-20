package com.runtheworld.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TerritoryDao {
    @Query("SELECT * FROM territories ORDER BY claimedAt DESC")
    fun observeAll(): Flow<List<TerritoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(territory: TerritoryEntity)

    @Query("DELETE FROM territories WHERE ownerUsername = :username")
    suspend fun deleteByOwner(username: String)

    @Query("UPDATE territories SET ownerColorHex = :colorHex WHERE ownerUsername = :username")
    suspend fun updateColorForOwner(username: String, colorHex: String)

    @Query("DELETE FROM territories WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM territories")
    suspend fun deleteAll()
}
