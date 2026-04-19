package com.runtheworld.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "friend_requests",
    indices = [Index(value = ["senderUid", "receiverUid"], unique = true)]
)
data class FriendRequestEntity(
    @PrimaryKey val id: String,
    val senderUid: String,
    val receiverUid: String,
    val status: String,   // "PENDING" | "ACCEPTED" | "DECLINED"
    val createdAt: Long
)
