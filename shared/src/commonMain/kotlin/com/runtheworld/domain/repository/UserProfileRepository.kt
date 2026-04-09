package com.runtheworld.domain.repository

import com.runtheworld.domain.model.UserProfile

interface UserProfileRepository {
    fun getProfile(): UserProfile?
    fun saveProfile(profile: UserProfile)
    fun isProfileSetUp(): Boolean
    fun updateStats(additionalAreaKm2: Double)
    fun clearProfile()
}
