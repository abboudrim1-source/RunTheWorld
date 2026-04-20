package com.runtheworld.domain.repository

import com.runtheworld.domain.model.UserProfile

interface UserProfileRepository {
    fun getProfile(): UserProfile?
    suspend fun saveProfile(profile: UserProfile)
    fun isProfileSetUp(): Boolean
    fun updateStats(additionalAreaKm2: Double)
    fun clearProfile()
    suspend fun syncToServer()
    suspend fun fetchFromServer(uid: String): UserProfile?
}
