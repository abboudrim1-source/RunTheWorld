package com.runtheworld.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.runtheworld.presentation.auth.AuthViewModel
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.ui.auth.AuthScreen
import com.runtheworld.ui.history.HistoryScreen
import com.runtheworld.ui.map.MapScreen
import com.runtheworld.ui.profile.ProfileScreen
import com.runtheworld.ui.profile.ProfileSetupScreen
import com.runtheworld.ui.run.RunScreen
import com.runtheworld.ui.welcome.WelcomeScreen
import org.koin.compose.viewmodel.koinViewModel

object Routes {
    const val WELCOME       = "welcome"
    const val AUTH          = "auth?signup={signup}"
    const val PROFILE_SETUP = "profile_setup"
    const val MAP           = "map"
    const val RUN           = "run"
    const val HISTORY       = "history"
    const val PROFILE       = "profile"

    fun auth(signUp: Boolean) = "auth?signup=$signUp"
}

@Composable
fun RunTheWorldNavHost(
    startDestination: String,
    navController: NavHostController = rememberNavController()
) {
    val profileViewModel: ProfileViewModel = koinViewModel()
    val authViewModel: AuthViewModel       = koinViewModel()

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onSignIn        = { navController.navigate(Routes.auth(signUp = false)) },
                onCreateAccount = { navController.navigate(Routes.auth(signUp = true)) }
            )
        }

        composable(
            route = Routes.AUTH,
            arguments = listOf(navArgument("signup") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) { entry ->
            val isSignUp = entry.arguments?.getBoolean("signup") ?: false
            AuthScreen(
                initialSignUpMode = isSignUp,
                onAuthSuccess = {
                    if (profileViewModel.isProfileSetUp) {
                        navController.navigate(Routes.MAP) { popUpTo(0) { inclusive = true } }
                    } else {
                        navController.navigate(Routes.PROFILE_SETUP) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.PROFILE_SETUP) {
            ProfileSetupScreen(
                onProfileSaved = {
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                onStartRun = { navController.navigate(Routes.RUN) },
                onHistory  = { navController.navigate(Routes.HISTORY) },
                onProfile  = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.RUN) {
            RunScreen(
                onRunFinished = {
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.MAP) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    profileViewModel.logout()
                    authViewModel.signOut {
                        navController.navigate(Routes.WELCOME) { popUpTo(0) { inclusive = true } }
                    }
                }
            )
        }
    }
}
