package com.runtheworld.data.repository

import com.runtheworld.domain.model.UserProfile
import com.runtheworld.domain.repository.UserProfileRepository
import com.russhwolf.settings.Settings

class UserProfileRepositoryImpl(private val settings: Settings) : UserProfileRepository {

    override fun isProfileSetUp(): Boolean = settings.getStringOrNull(KEY_USERNAME) != null

    override fun getProfile(): UserProfile? {
        val username = settings.getStringOrNull(KEY_USERNAME) ?: return null
        return UserProfile(
            username = username,
            colorHex = settings.getString(KEY_COLOR, DEFAULT_COLOR),
            totalAreaKm2 = settings.getDouble(KEY_TOTAL_AREA, 0.0),
            runCount = settings.getInt(KEY_RUN_COUNT, 0)
        )
    }

    override fun saveProfile(profile: UserProfile) {
        settings.putString(KEY_USERNAME, profile.username)
        settings.putString(KEY_COLOR, profile.colorHex)
        settings.putDouble(KEY_TOTAL_AREA, profile.totalAreaKm2)
        settings.putInt(KEY_RUN_COUNT, profile.runCount)
    }

    override fun updateStats(additionalAreaKm2: Double) {
        val current = getProfile() ?: return
        saveProfile(
            current.copy(
                totalAreaKm2 = current.totalAreaKm2 + additionalAreaKm2,
                runCount = current.runCount + 1
            )
        )
    }

    companion object {
        private const val KEY_USERNAME = "profile_username"
        private const val KEY_COLOR = "profile_color"
        private const val KEY_TOTAL_AREA = "profile_total_area"
        private const val KEY_RUN_COUNT = "profile_run_count"
        private const val DEFAULT_COLOR = "#FF5733"
    }
}
