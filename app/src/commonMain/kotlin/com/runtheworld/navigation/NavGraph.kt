package com.runtheworld.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.runtheworld.ui.history.HistoryScreen
import com.runtheworld.ui.map.MapScreen
import com.runtheworld.ui.profile.ProfileSetupScreen
import com.runtheworld.ui.run.RunScreen

object Routes {
    const val PROFILE_SETUP = "profile_setup"
    const val MAP = "map"
    const val RUN = "run"
    const val HISTORY = "history"
}

@Composable
fun RunTheWorldNavHost(
    startDestination: String,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.PROFILE_SETUP) {
            ProfileSetupScreen(
                onProfileSaved = {
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.PROFILE_SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                onStartRun = { navController.navigate(Routes.RUN) },
                onHistory = { navController.navigate(Routes.HISTORY) }
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
    }
}
