package com.runtheworld

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.runtheworld.navigation.Routes
import com.runtheworld.navigation.RunTheWorldNavHost
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.ui.theme.RunTheWorldTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    RunTheWorldTheme {
        val profileViewModel = koinViewModel<ProfileViewModel>()
        val startDestination = if (profileViewModel.isProfileSetUp) Routes.MAP else Routes.PROFILE_SETUP
        RunTheWorldNavHost(startDestination = startDestination)
    }
}
