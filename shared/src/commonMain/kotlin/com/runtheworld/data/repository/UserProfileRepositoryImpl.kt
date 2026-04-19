package com.runtheworld.data.repository

import com.runtheworld.data.local.db.UserProfileDao
import com.runtheworld.data.local.db.UserProfileEntity
import com.runtheworld.domain.model.UserProfile
import com.runtheworld.domain.repository.UserProfileRepository
import com.russhwolf.settings.Settings

class UserProfileRepositoryImpl(
    private val settings: Settings,
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    // Prefix all profile keys with the signed-in UID so each account has its own data
    private fun prefix(): String {
        val uid = settings.getStringOrNull("auth_uid") ?: "anonymous"
        return "${uid}_"
    }

    override fun isProfileSetUp(): Boolean =
        settings.getStringOrNull(prefix() + KEY_USERNAME) != null

    override fun getProfile(): UserProfile? {
        val p = prefix()
        val username = settings.getStringOrNull(p + KEY_USERNAME) ?: return null
        return UserProfile(
            username = username,
            displayName = settings.getString(p + KEY_DISPLAY_NAME, ""),
            colorHex = settings.getString(p + KEY_COLOR, DEFAULT_COLOR),
            totalAreaKm2 = settings.getDouble(p + KEY_TOTAL_AREA, 0.0),
            runCount = settings.getInt(p + KEY_RUN_COUNT, 0)
        )
    }

    override suspend fun saveProfile(profile: UserProfile) {
        val p = prefix()
        settings.putString(p + KEY_USERNAME, profile.username)
        settings.putString(p + KEY_DISPLAY_NAME, profile.displayName)
        settings.putString(p + KEY_COLOR, profile.colorHex)
        settings.putDouble(p + KEY_TOTAL_AREA, profile.totalAreaKm2)
        settings.putInt(p + KEY_RUN_COUNT, profile.runCount)
        // Also persist to Room so other users can search by username
        val uid = settings.getStringOrNull("auth_uid") ?: return
        userProfileDao.upsert(
            UserProfileEntity(
                uid = uid,
                username = profile.username,
                displayName = profile.displayName,
                colorHex = profile.colorHex
            )
        )
    }

    override fun clearProfile() {
        val p = prefix()
        settings.remove(p + KEY_USERNAME)
        settings.remove(p + KEY_DISPLAY_NAME)
        settings.remove(p + KEY_COLOR)
        settings.remove(p + KEY_TOTAL_AREA)
        settings.remove(p + KEY_RUN_COUNT)
    }

    override fun updateStats(additionalAreaKm2: Double) {
        val current = getProfile() ?: return
        val p = prefix()
        settings.putDouble(p + KEY_TOTAL_AREA, current.totalAreaKm2 + additionalAreaKm2)
        settings.putInt(p + KEY_RUN_COUNT, current.runCount + 1)
    }

    companion object {
        private const val KEY_USERNAME     = "profile_username"
        private const val KEY_DISPLAY_NAME = "profile_display_name"
        private const val KEY_COLOR        = "profile_color"
        private const val KEY_TOTAL_AREA   = "profile_total_area"
        private const val KEY_RUN_COUNT    = "profile_run_count"
        private const val DEFAULT_COLOR    = "#FF5733"
    }
}
