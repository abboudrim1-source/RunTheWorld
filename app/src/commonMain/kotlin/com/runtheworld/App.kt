package com.runtheworld

import androidx.compose.runtime.*
import com.runtheworld.navigation.Routes
import com.runtheworld.navigation.RunTheWorldNavHost
import com.runtheworld.presentation.auth.AuthViewModel
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.ui.theme.RunTheWorldTheme
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    RunTheWorldTheme {
        val authViewModel    = koinViewModel<AuthViewModel>()
        val profileViewModel = koinViewModel<ProfileViewModel>()
        var startDestination by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            scope.launch {
                val sessionValid = authViewModel.validateAndGetStartDestination()
                startDestination = when {
                    !sessionValid                      -> Routes.WELCOME
                    !profileViewModel.isProfileSetUp   -> Routes.PROFILE_SETUP
                    else                               -> Routes.MAP
                }
            }
        }

        if (startDestination != null) {
            RunTheWorldNavHost(startDestination = startDestination!!)
        }
    }
}
