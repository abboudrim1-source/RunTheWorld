package com.runtheworld.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

/**
 * Replace this with your OAuth 2.0 Web Client ID from Google Cloud Console.
 * Steps:
 *  1. Go to https://console.cloud.google.com/
 *  2. APIs & Services → Credentials
 *  3. Copy the "Web client (auto created by Google Service)" Client ID
 */
private const val WEB_CLIENT_ID = "501526936412-49t3lrtki4o4lfv8puo4lmt7qc9ar4n9.apps.googleusercontent.com"

@Composable
actual fun rememberGoogleSignInLauncher(
    onSuccess: (GoogleUser) -> Unit,
    onError: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    return {
        scope.launch {
            try {
                val option = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(option)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
                onSuccess(
                    GoogleUser(
                        id          = credential.id,
                        email       = credential.id,   // id is the email for GoogleIdTokenCredential
                        displayName = credential.displayName
                    )
                )
            } catch (e: GetCredentialException) {
                onError(e.message ?: "Google sign-in cancelled")
            } catch (e: Exception) {
                onError(e.message ?: "Google sign-in failed")
            }
        }
    }
}
