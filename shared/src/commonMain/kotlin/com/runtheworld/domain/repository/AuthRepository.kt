package com.runtheworld.domain.repository

import com.runtheworld.domain.model.AuthUser

interface AuthRepository {
    fun getCurrentUser(): AuthUser?
    suspend fun signInWithEmail(email: String, password: String): AuthUser
    suspend fun signUpWithEmail(email: String, password: String): AuthUser
    suspend fun signInWithGoogle(googleId: String, email: String, displayName: String?): AuthUser
    suspend fun signOut()
    suspend fun validateSession(): Boolean
}
