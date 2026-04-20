package com.runtheworld.data.repository

import com.runtheworld.data.local.db.UserAccountDao
import com.runtheworld.data.local.db.UserAccountEntity
import com.runtheworld.domain.model.AuthUser
import com.runtheworld.domain.repository.AuthRepository
import com.runtheworld.util.generateSalt
import com.runtheworld.util.hashPassword
import com.russhwolf.settings.Settings
import kotlin.random.Random

class RoomAuthRepositoryImpl(
    private val dao: UserAccountDao,
    private val settings: Settings
) : AuthRepository {

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

    override suspend fun signInWithEmail(email: String, password: String): AuthUser {
        val account = dao.findByEmail(email.lowercase())
            ?: throw Exception("No account found with that email")
        if (account.loginType != "email")
            throw Exception("This email is linked to a Google account. Please sign in with Google.")
        val hash = hashPassword(password, account.salt!!)
        if (hash != account.passwordHash)
            throw Exception("Incorrect password")
        return saveSession(account)
    }

    override suspend fun signUpWithEmail(email: String, password: String): AuthUser {
        val normalized = email.lowercase()
        if (dao.emailExists(normalized) > 0)
            throw Exception("An account with this email already exists")
        val salt = generateSalt()
        val account = UserAccountEntity(
            uid         = randomUid(),
            email       = normalized,
            displayName = null,
            passwordHash = hashPassword(password, salt),
            salt        = salt,
            loginType   = "email"
        )
        dao.insert(account)
        return saveSession(account)
    }

    override suspend fun signInWithGoogle(
        googleId: String,
        email: String,
        displayName: String?
    ): AuthUser {
        val existing = dao.findByGoogleId(googleId)
        val account = if (existing != null) {
            existing
        } else {
            // First time Google sign-in — create a local record
            UserAccountEntity(
                uid         = googleId,
                email       = email.lowercase(),
                displayName = displayName,
                passwordHash = null,
                salt        = null,
                loginType   = "google"
            ).also { dao.insert(it) }
        }
        return saveSession(account)
    }

    override suspend fun signOut() {
        settings.remove(KEY_UID)
        settings.remove(KEY_EMAIL)
        settings.remove(KEY_NAME)
    }

    override suspend fun validateSession(): Boolean {
        val uid = settings.getStringOrNull(KEY_UID) ?: return false
        return dao.findByGoogleId(uid) != null || dao.findByEmail(
            settings.getStringOrNull(KEY_EMAIL) ?: return false
        ) != null
    }

    private fun saveSession(account: UserAccountEntity): AuthUser {
        settings.putString(KEY_UID, account.uid)
        settings.putString(KEY_EMAIL, account.email)
        account.displayName?.let { settings.putString(KEY_NAME, it) }
        return AuthUser(uid = account.uid, email = account.email, displayName = account.displayName)
    }

    private fun randomUid(): String =
        Random.nextBytes(16).joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
}
