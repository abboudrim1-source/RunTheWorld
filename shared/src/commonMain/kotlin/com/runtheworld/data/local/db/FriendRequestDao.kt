package com.runtheworld.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FriendRequestDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(request: FriendRequestEntity)

    @Query("UPDATE friend_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    /** Pending requests sent TO me (my inbox). */
    @Query("""
        SELECT * FROM friend_requests
        WHERE receiverUid = :uid AND status = 'PENDING'
        ORDER BY createdAt DESC
    """)
    suspend fun getInboxRequests(uid: String): List<FriendRequestEntity>

    /** UIDs of users I've sent a still-pending request to. */
    @Query("""
        SELECT receiverUid FROM friend_requests
        WHERE senderUid = :uid AND status = 'PENDING'
    """)
    suspend fun getSentPendingReceiverUids(uid: String): List<String>

    /** Friends = accepted requests in either direction. */
    @Query("""
        SELECT p.* FROM user_profiles p
        WHERE p.uid IN (
            SELECT CASE WHEN r.senderUid = :uid THEN r.receiverUid ELSE r.senderUid END
            FROM friend_requests r
            WHERE (r.senderUid = :uid OR r.receiverUid = :uid) AND r.status = 'ACCEPTED'
        )
        ORDER BY p.username ASC
    """)
    suspend fun getFriends(uid: String): List<UserProfileEntity>

    @Query("""
        SELECT COUNT(*) FROM friend_requests
        WHERE receiverUid = :uid AND status = 'PENDING'
    """)
    suspend fun getPendingInboxCount(uid: String): Int
}
