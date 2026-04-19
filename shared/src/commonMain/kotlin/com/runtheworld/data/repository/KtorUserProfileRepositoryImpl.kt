package com.runtheworld.data.repository

import com.runtheworld.data.local.db.UserProfileDao
import com.runtheworld.data.local.db.UserProfileEntity
import com.runtheworld.domain.model.UserProfile
import com.runtheworld.domain.repository.UserProfileRepository
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class ProfileSyncRequest(
    val uid: String,
    val username: String,
    val displayName: String,
    val colorHex: String,
    val totalAreaKm2: Double,
    val runCount: Int
)

class KtorUserProfileRepositoryImpl(
    private val settings: Settings,
    private val userProfileDao: UserProfileDao,
    private val httpClient: HttpClient
) : UserProfileRepository {

    private val baseUrl = "http://10.0.2.2:8080" 

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
        val uid = settings.getStringOrNull("auth_uid")
        
        if (uid == null) {
            println("DEBUG: Sync skipped - auth_uid is NULL")
            saveLocally(profile, "anonymous")
            return
        }

        println("DEBUG: Attempting sync for user: $uid to $baseUrl")
        saveLocally(profile, uid)

        try {
            val response = httpClient.post("$baseUrl/profiles") {
                contentType(ContentType.Application.Json)
                setBody(ProfileSyncRequest(
                    uid = uid,
                    username = profile.username,
                    displayName = profile.displayName,
                    colorHex = profile.colorHex,
                    totalAreaKm2 = profile.totalAreaKm2,
                    runCount = profile.runCount
                ))
            }
            println("DEBUG: Sync successful. Response: ${response.status}")
        } catch (e: Exception) {
            println("DEBUG: Sync failed: ${e.message}")
        }
    }

    private suspend fun saveLocally(profile: UserProfile, uid: String) {
        val p = prefix()
        settings.putString(p + KEY_USERNAME, profile.username)
        settings.putString(p + KEY_DISPLAY_NAME, profile.displayName)
        settings.putString(p + KEY_COLOR, profile.colorHex)
        settings.putDouble(p + KEY_TOTAL_AREA, profile.totalAreaKm2)
        settings.putInt(p + KEY_RUN_COUNT, profile.runCount)

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
        val newArea = current.totalAreaKm2 + additionalAreaKm2
        val newCount = current.runCount + 1
        
        settings.putDouble(p + KEY_TOTAL_AREA, newArea)
        settings.putInt(p + KEY_RUN_COUNT, newCount)
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
