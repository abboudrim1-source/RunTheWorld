package com.runtheworld.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE uid = :googleId LIMIT 1")
    suspend fun findByGoogleId(googleId: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(account: UserAccountEntity)

    @Query("SELECT COUNT(*) FROM user_accounts WHERE email = :email")
    suspend fun emailExists(email: String): Int

    @Query("DELETE FROM user_accounts")
    suspend fun deleteAll()
}
