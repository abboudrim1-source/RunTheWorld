package com.runtheworld.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.runtheworld.presentation.auth.AuthViewModel
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.ui.auth.AuthScreen
import com.runtheworld.ui.friends.AddFriendsScreen
import com.runtheworld.ui.friends.InboxScreen
import com.runtheworld.ui.history.HistoryScreen
import com.runtheworld.ui.leaderboard.LeaderboardScreen
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
    const val ADD_FRIENDS   = "add_friends"
    const val INBOX         = "inbox"
    const val LEADERBOARD   = "leaderboard"

    fun auth(signUp: Boolean) = "auth?signup=$signUp"
}

@Composable
fun RunTheWorldNavHost(
    startDestination: String,
    navController: NavHostController = rememberNavController()
) {
    val profileViewModel: ProfileViewModel = koinViewModel()
    val authViewModel: AuthViewModel       = koinViewModel()
    val scope = rememberCoroutineScope()

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
                    scope.launch {
                        val uid = authViewModel.currentUid
                        if (!profileViewModel.isProfileSetUp && uid != null) {
                            profileViewModel.tryRestoreFromServer(uid)
                        }
                        if (profileViewModel.isProfileSetUp) {
                            navController.navigate(Routes.MAP) { popUpTo(0) { inclusive = true } }
                        } else {
                            navController.navigate(Routes.PROFILE_SETUP) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
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
                onStartRun    = { navController.navigate(Routes.RUN) },
                onHistory     = { navController.navigate(Routes.HISTORY) },
                onProfile     = { navController.navigate(Routes.PROFILE) },
                onInbox       = { navController.navigate(Routes.INBOX) },
                onLeaderboard = { navController.navigate(Routes.LEADERBOARD) }
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
                onAddFriends = { navController.navigate(Routes.ADD_FRIENDS) },
                onSignOut = {
                    profileViewModel.logout()
                    authViewModel.signOut {
                        navController.navigate(Routes.WELCOME) { popUpTo(0) { inclusive = true } }
                    }
                }
            )
        }

        composable(Routes.ADD_FRIENDS) {
            AddFriendsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.INBOX) {
            InboxScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
    }
}
