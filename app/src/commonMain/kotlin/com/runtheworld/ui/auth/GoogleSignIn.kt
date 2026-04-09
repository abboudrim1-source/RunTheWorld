package com.runtheworld.ui.auth

import androidx.compose.runtime.Composable

data class GoogleUser(val id: String, val email: String, val displayName: String?)

/**
 * Returns a lambda that, when invoked, launches the Google Sign-In flow.
 * On success calls [onSuccess] with the user's Google ID, email, and display name.
 * On failure calls [onError] with a human-readable message.
 */
@Composable
expect fun rememberGoogleSignInLauncher(
    onSuccess: (GoogleUser) -> Unit,
    onError: (String) -> Unit
): () -> Unit
