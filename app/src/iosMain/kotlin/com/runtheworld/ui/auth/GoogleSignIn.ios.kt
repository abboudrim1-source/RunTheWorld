package com.runtheworld.ui.auth

import androidx.compose.runtime.Composable

@Composable
actual fun rememberGoogleSignInLauncher(
    onSuccess: (GoogleUser) -> Unit,
    onError: (String) -> Unit
): () -> Unit = { onError("Google Sign-In is not yet supported on iOS") }
