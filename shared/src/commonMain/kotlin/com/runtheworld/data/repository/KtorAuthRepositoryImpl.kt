package com.runtheworld.data.repository

import com.runtheworld.data.network.NetworkConfig
import com.runtheworld.domain.model.AuthUser
import com.runtheworld.domain.repository.AuthRepository
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable private data class SignUpBody(val email: String, val password: String, val displayName: String? = null)
@Serializable private data class SignInBody(val email: String, val password: String)
@Serializable private data class GoogleSignInBody(val googleId: String, val email: String, val displayName: String? = null)
@Serializable private data class AuthResponseDto(val uid: String, val email: String, val displayName: String?)

class KtorAuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val settings: Settings
) : AuthRepository {

    private val baseUrl get() = NetworkConfig.BASE_URL

    companion object {
        private const val KEY_UID   = "auth_uid"
        private const val KEY_EMAIL = "auth_email"
        private const val KEY_NAME  = "auth_display_name"
    }

    override fun getCurrentUser(): AuthUser? {
        val uid = settings.getStringOrNull(KEY_UID) ?: return null
        return AuthUser(
            uid = uid,
            email = settings.getStringOrNull(KEY_EMAIL),
            displayName = settings.getStringOrNull(KEY_NAME)
        )
    }

    override suspend fun signUpWithEmail(email: String, password: String): AuthUser {
        val response = httpClient.post("$baseUrl/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(SignUpBody(email = email.trim().lowercase(), password = password))
        }
        if (!response.status.isSuccess()) {
            val err = runCatching { response.body<Map<String, String>>()["error"] }.getOrNull()
            throw Exception(err ?: "Sign-up failed")
        }
        return saveSession(response.body<AuthResponseDto>())
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthUser {
        val response = httpClient.post("$baseUrl/auth/signin") {
            contentType(ContentType.Application.Json)
            setBody(SignInBody(email = email.trim().lowercase(), password = password))
        }
        if (!response.status.isSuccess()) {
            val err = runCatching { response.body<Map<String, String>>()["error"] }.getOrNull()
            throw Exception(err ?: "Sign-in failed")
        }
        return saveSession(response.body<AuthResponseDto>())
    }

    override suspend fun signInWithGoogle(googleId: String, email: String, displayName: String?): AuthUser {
        val response = httpClient.post("$baseUrl/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(GoogleSignInBody(googleId = googleId, email = email, displayName = displayName))
        }
        if (!response.status.isSuccess()) {
            val err = runCatching { response.body<Map<String, String>>()["error"] }.getOrNull()
            throw Exception(err ?: "Google sign-in failed")
        }
        return saveSession(response.body<AuthResponseDto>())
    }

    override suspend fun signOut() {
        settings.remove(KEY_UID)
        settings.remove(KEY_EMAIL)
        settings.remove(KEY_NAME)
    }

    override suspend fun validateSession(): Boolean {
        val uid = settings.getStringOrNull(KEY_UID) ?: return false
        return try {
            val response = httpClient.get("$baseUrl/auth/verify/$uid")
            response.status.isSuccess()
        } catch (_: Exception) {
            true // keep session if server unreachable (offline support)
        }
    }

    private fun saveSession(dto: AuthResponseDto): AuthUser {
        settings.putString(KEY_UID, dto.uid)
        settings.putString(KEY_EMAIL, dto.email)
        dto.displayName?.let { settings.putString(KEY_NAME, it) }
        return AuthUser(uid = dto.uid, email = dto.email, displayName = dto.displayName)
    }
}
