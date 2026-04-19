package com.runtheworld.domain.model

data class FriendRequest(
    val id: String,
    val sender: FriendUser,
    val status: RequestStatus
)

enum class RequestStatus { PENDING, ACCEPTED, DECLINED }
