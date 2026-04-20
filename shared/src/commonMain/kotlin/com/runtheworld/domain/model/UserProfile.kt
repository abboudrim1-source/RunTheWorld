package com.runtheworld.domain.model

data class UserProfile(
    val username: String,
    val displayName: String = "",
    val colorHex: String,
    val totalAreaKm2: Double = 0.0,
    val runCount: Int = 0,
    val avatarBase64: String? = null,
    val city: String? = null
)
