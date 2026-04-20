package com.runtheworld.domain.model

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val totalAreaKm2: Double,
    val runCount: Int,
    val colorHex: String
)
