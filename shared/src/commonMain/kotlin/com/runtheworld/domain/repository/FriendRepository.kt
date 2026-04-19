package com.runtheworld.domain.repository

import com.runtheworld.domain.model.FriendRequest
import com.runtheworld.domain.model.FriendUser

interface FriendRepository {
    suspend fun searchUsers(query: String): List<FriendUser>
    suspend fun sendFriendRequest(receiverUid: String)
    suspend fun acceptRequest(requestId: String)
    suspend fun declineRequest(requestId: String)
    suspend fun getFriends(): List<FriendUser>
    suspend fun getInbox(): List<FriendRequest>
    suspend fun getPendingInboxCount(): Int
    suspend fun getSentPendingUids(): Set<String>
    suspend fun getFriendUids(): Set<String>
}
