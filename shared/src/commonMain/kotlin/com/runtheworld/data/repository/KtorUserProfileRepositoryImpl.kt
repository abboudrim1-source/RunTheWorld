package com.runtheworld.data.repository

import com.runtheworld.data.local.db.UserProfileDao
import com.runtheworld.data.local.db.UserProfileEntity
import com.runtheworld.domain.model.UserProfile
import com.runtheworld.domain.repository.UserProfileRepository
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.call.*
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
    val runCount: Int,
    val avatarBase64: String? = null,
    val city: String? = null
)

class KtorUserProfileRepositoryImpl(
    private val settings: Settings,
    private val userProfileDao: UserProfileDao,
    private val httpClient: HttpClient
) : UserProfileRepository {

    private val baseUrl = com.runtheworld.data.network.NetworkConfig.BASE_URL

    private fun prefix(): String {
        val uid = settings.getStringOrNull("auth_uid") ?: "anonymous"
        return "${uid}_"
    }

    override fun getCurrentUid(): String? = settings.getStringOrNull("auth_uid")

    override fun isProfileSetUp(): Boolean =
        settings.getStringOrNull(prefix() + KEY_USERNAME) != null

    override fun getProfile(): UserProfile? {
        val p = prefix()
        val username = settings.getStringOrNull(p + KEY_USERNAME) ?: return null
        return UserProfile(
            username     = username,
            displayName  = settings.getString(p + KEY_DISPLAY_NAME, ""),
            colorHex     = settings.getString(p + KEY_COLOR, DEFAULT_COLOR),
            totalAreaKm2 = settings.getDouble(p + KEY_TOTAL_AREA, 0.0),
            runCount     = settings.getInt(p + KEY_RUN_COUNT, 0),
            avatarBase64 = settings.getStringOrNull(p + KEY_AVATAR),
            city         = settings.getStringOrNull(p + KEY_CITY)
        )
    }

    override suspend fun saveProfile(profile: UserProfile) {
        val uid = settings.getStringOrNull("auth_uid")
        if (uid == null) { saveLocally(profile, "anonymous"); return }

        val response = try {
            httpClient.post("$baseUrl/profiles") {
                contentType(ContentType.Application.Json)
                setBody(ProfileSyncRequest(
                    uid          = uid,
                    username     = profile.username,
                    displayName  = profile.displayName,
                    colorHex     = profile.colorHex,
                    totalAreaKm2 = profile.totalAreaKm2,
                    runCount     = profile.runCount,
                    avatarBase64 = profile.avatarBase64,
                    city         = profile.city
                ))
            }
        } catch (_: Exception) {
            saveLocally(profile, uid)
            return
        }

        if (!response.status.isSuccess()) {
            val err = runCatching { response.body<Map<String, String>>()["error"] }.getOrNull()
            throw Exception(err ?: "Failed to save profile")
        }

        saveLocally(profile, uid)
    }

    private suspend fun saveLocally(profile: UserProfile, uid: String) {
        val p = prefix()
        settings.putString(p + KEY_USERNAME, profile.username)
        settings.putString(p + KEY_DISPLAY_NAME, profile.displayName)
        settings.putString(p + KEY_COLOR, profile.colorHex)
        settings.putDouble(p + KEY_TOTAL_AREA, profile.totalAreaKm2)
        settings.putInt(p + KEY_RUN_COUNT, profile.runCount)
        profile.avatarBase64?.let { settings.putString(p + KEY_AVATAR, it) }
        profile.city?.let { settings.putString(p + KEY_CITY, it) }

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
        settings.remove(p + KEY_AVATAR)
        settings.remove(p + KEY_CITY)
    }

    override fun updateStats(additionalAreaKm2: Double) {
        val current = getProfile() ?: return
        val p = prefix()
        settings.putDouble(p + KEY_TOTAL_AREA, current.totalAreaKm2 + additionalAreaKm2)
        settings.putInt(p + KEY_RUN_COUNT, current.runCount + 1)
    }

    override suspend fun fetchFromServer(uid: String): UserProfile? {
        return try {
            val response = httpClient.get("$baseUrl/profiles/$uid")
            if (!response.status.isSuccess()) return null
            val dto = response.body<ProfileSyncRequest>()
            UserProfile(
                username     = dto.username,
                displayName  = dto.displayName,
                colorHex     = dto.colorHex,
                totalAreaKm2 = dto.totalAreaKm2,
                runCount     = dto.runCount,
                avatarBase64 = dto.avatarBase64,
                city         = dto.city
            )
        } catch (_: Exception) { null }
    }

    override suspend fun syncToServer() {
        val uid = settings.getStringOrNull("auth_uid") ?: return
        val profile = getProfile() ?: return
        try {
            httpClient.post("$baseUrl/profiles") {
                contentType(ContentType.Application.Json)
                setBody(ProfileSyncRequest(
                    uid          = uid,
                    username     = profile.username,
                    displayName  = profile.displayName,
                    colorHex     = profile.colorHex,
                    totalAreaKm2 = profile.totalAreaKm2,
                    runCount     = profile.runCount,
                    avatarBase64 = profile.avatarBase64,
                    city         = profile.city
                ))
            }
        } catch (_: Exception) {}
    }

    companion object {
        private const val KEY_USERNAME     = "profile_username"
        private const val KEY_DISPLAY_NAME = "profile_display_name"
        private const val KEY_COLOR        = "profile_color"
        private const val KEY_TOTAL_AREA   = "profile_total_area"
        private const val KEY_RUN_COUNT    = "profile_run_count"
        private const val KEY_AVATAR       = "profile_avatar"
        private const val KEY_CITY         = "profile_city"
        private const val DEFAULT_COLOR    = "#FF5733"
    }
}
