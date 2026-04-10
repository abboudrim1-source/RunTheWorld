package com.runtheworld.domain.model

data class UserProfile(
    val username: String,
    val displayName: String = "",
    val colorHex: String,   // e.g. "#FF5733" — used for territory tint
    val totalAreaKm2: Double = 0.0,
    val runCount: Int = 0
)
