package com.runtheworld.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE uid = :uid LIMIT 1")
    suspend fun getByUid(uid: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE username LIKE '%' || :query || '%' AND uid != :excludeUid ORDER BY username ASC")
    suspend fun searchByUsername(query: String, excludeUid: String): List<UserProfileEntity>
}
