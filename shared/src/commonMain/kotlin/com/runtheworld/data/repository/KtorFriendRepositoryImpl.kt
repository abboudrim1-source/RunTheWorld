package com.runtheworld.data.repository

import com.russhwolf.settings.Settings
import com.runtheworld.domain.model.FriendRequest
import com.runtheworld.domain.model.FriendUser
import com.runtheworld.domain.model.RequestStatus
import com.runtheworld.domain.repository.FriendRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

// ── DTOs ──────────────────────────────────────────────────────────────────────

@Serializable
private data class UserDto(
    val uid: String,
    val username: String,
    val displayName: String,
    val colorHex: String,
    val totalAreaKm2: Double = 0.0,
    val runCount: Int = 0
)

@Serializable
private data class FriendRequestDto(
    val id: String,
    val senderUid: String,
    val senderUsername: String,
    val senderDisplayName: String,
    val senderColorHex: String,
    val status: String,
    val createdAt: Long
)

@Serializable
private data class SendRequestBody(val senderUid: String, val receiverUid: String)

// ── Implementation ─────────────────────────────────────────────────────────────

class KtorFriendRepositoryImpl(
    private val httpClient: HttpClient,
    private val settings: Settings
) : FriendRepository {

    private val baseUrl = com.runtheworld.data.network.NetworkConfig.BASE_URL
    private fun uid() = settings.getStringOrNull("auth_uid") ?: ""

    override suspend fun searchUsers(query: String): List<FriendUser> {
        if (query.isBlank()) return emptyList()
        return try {
            httpClient.get("$baseUrl/profiles/search") {
                parameter("q", query.trim())
                parameter("excludeUid", uid())
            }.body<List<UserDto>>().map { it.toFriendUser() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun sendFriendRequest(receiverUid: String) {
        httpClient.post("$baseUrl/friend-requests") {
            contentType(ContentType.Application.Json)
            setBody(SendRequestBody(senderUid = uid(), receiverUid = receiverUid))
        }
    }

    override suspend fun acceptRequest(requestId: String) {
        httpClient.patch("$baseUrl/friend-requests/$requestId/accept")
    }

    override suspend fun declineRequest(requestId: String) {
        httpClient.patch("$baseUrl/friend-requests/$requestId/decline")
    }

    override suspend fun getFriends(): List<FriendUser> {
        return try {
            httpClient.get("$baseUrl/friends") {
                parameter("uid", uid())
            }.body<List<UserDto>>().map { it.toFriendUser() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getInbox(): List<FriendRequest> {
        return try {
            httpClient.get("$baseUrl/friend-requests/inbox") {
                parameter("uid", uid())
            }.body<List<FriendRequestDto>>().map { dto ->
                FriendRequest(
                    id = dto.id,
                    sender = FriendUser(
                        uid = dto.senderUid,
                        username = dto.senderUsername,
                        displayName = dto.senderDisplayName,
                        colorHex = dto.senderColorHex
                    ),
                    status = RequestStatus.PENDING
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getPendingInboxCount(): Int {
        return try {
            httpClient.get("$baseUrl/friend-requests/inbox") {
                parameter("uid", uid())
            }.body<List<FriendRequestDto>>().size
        } catch (_: Exception) {
            0
        }
    }

    override suspend fun getSentPendingUids(): Set<String> {
        return try {
            httpClient.get("$baseUrl/friend-requests/sent-pending") {
                parameter("uid", uid())
            }.body<List<String>>().toSet()
        } catch (_: Exception) {
            emptySet()
        }
    }

    override suspend fun getFriendUids(): Set<String> = getFriends().map { it.uid }.toSet()

    private fun UserDto.toFriendUser() = FriendUser(
        uid = uid,
        username = username,
        displayName = displayName,
        colorHex = colorHex
    )
}
