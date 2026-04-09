package com.runtheworld

import androidx.compose.runtime.Composable
import com.runtheworld.navigation.Routes
import com.runtheworld.navigation.RunTheWorldNavHost
import com.runtheworld.presentation.auth.AuthViewModel
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.ui.theme.RunTheWorldTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    RunTheWorldTheme {
        val authViewModel    = koinViewModel<AuthViewModel>()
        val profileViewModel = koinViewModel<ProfileViewModel>()

        val startDestination = when {
            !authViewModel.isSignedIn          -> Routes.WELCOME
            !profileViewModel.isProfileSetUp   -> Routes.PROFILE_SETUP
            else                               -> Routes.MAP
        }

        RunTheWorldNavHost(startDestination = startDestination)
    }
}
