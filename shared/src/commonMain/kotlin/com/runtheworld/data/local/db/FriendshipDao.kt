package com.runtheworld.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FriendshipDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFriend(friendship: FriendshipEntity)

    @Query("DELETE FROM friendships WHERE ownerUid = :ownerUid AND friendUid = :friendUid")
    suspend fun removeFriend(ownerUid: String, friendUid: String)

    @Query("""
        SELECT p.* FROM user_profiles p
        INNER JOIN friendships f ON p.uid = f.friendUid
        WHERE f.ownerUid = :ownerUid
        ORDER BY p.username ASC
    """)
    suspend fun getFriendProfiles(ownerUid: String): List<UserProfileEntity>

    @Query("SELECT COUNT(*) FROM friendships WHERE ownerUid = :ownerUid AND friendUid = :friendUid")
    suspend fun isFriend(ownerUid: String, friendUid: String): Int
}
