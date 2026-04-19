package com.runtheworld.data.local.db

import androidx.room.Entity

@Entity(
    tableName = "friendships",
    primaryKeys = ["ownerUid", "friendUid"]
)
data class FriendshipEntity(
    val ownerUid: String,
    val friendUid: String
)
