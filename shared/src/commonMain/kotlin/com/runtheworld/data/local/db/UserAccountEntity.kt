package com.runtheworld.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_accounts",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserAccountEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String?,
    val passwordHash: String?,  // null for Google accounts
    val salt: String?,          // null for Google accounts
    val loginType: String       // "email" or "google"
)
