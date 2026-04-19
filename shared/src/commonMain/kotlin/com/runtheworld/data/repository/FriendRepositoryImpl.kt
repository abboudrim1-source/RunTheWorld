package com.runtheworld.data.repository

import com.runtheworld.data.local.db.FriendRequestDao
import com.runtheworld.data.local.db.FriendRequestEntity
import com.runtheworld.data.local.db.UserProfileDao
import com.runtheworld.data.local.db.UserProfileEntity
import com.runtheworld.domain.model.FriendRequest
import com.runtheworld.domain.model.FriendUser
import com.runtheworld.domain.model.RequestStatus
import com.runtheworld.domain.repository.FriendRepository
import com.runtheworld.presentation.run.currentTimeMillis
import com.russhwolf.settings.Settings
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class FriendRepositoryImpl(
    private val friendRequestDao: FriendRequestDao,
    private val userProfileDao: UserProfileDao,
    private val settings: Settings
) : FriendRepository {

    private fun currentUid(): String = settings.getStringOrNull("auth_uid") ?: ""

    override suspend fun searchUsers(query: String): List<FriendUser> {
        if (query.isBlank()) return emptyList()
        return userProfileDao.searchByUsername(query.trim(), currentUid())
            .map { it.toFriendUser() }
    }

    override suspend fun sendFriendRequest(receiverUid: String) {
        friendRequestDao.insert(
            FriendRequestEntity(
                id = Uuid.random().toString(),
                senderUid = currentUid(),
                receiverUid = receiverUid,
                status = "PENDING",
                createdAt = currentTimeMillis()
            )
        )
    }

    override suspend fun acceptRequest(requestId: String) {
        friendRequestDao.updateStatus(requestId, "ACCEPTED")
    }

    override suspend fun declineRequest(requestId: String) {
        friendRequestDao.updateStatus(requestId, "DECLINED")
    }

    override suspend fun getFriends(): List<FriendUser> =
        friendRequestDao.getFriends(currentUid()).map { it.toFriendUser() }

    override suspend fun getInbox(): List<FriendRequest> =
        friendRequestDao.getInboxRequests(currentUid()).mapNotNull { req ->
            val profile = userProfileDao.getByUid(req.senderUid) ?: return@mapNotNull null
            FriendRequest(id = req.id, sender = profile.toFriendUser(), status = RequestStatus.PENDING)
        }

    override suspend fun getPendingInboxCount(): Int =
        friendRequestDao.getPendingInboxCount(currentUid())

    override suspend fun getSentPendingUids(): Set<String> =
        friendRequestDao.getSentPendingReceiverUids(currentUid()).toSet()

    override suspend fun getFriendUids(): Set<String> =
        getFriends().map { it.uid }.toSet()

    private fun UserProfileEntity.toFriendUser() =
        FriendUser(uid = uid, username = username, displayName = displayName, colorHex = colorHex)
}
