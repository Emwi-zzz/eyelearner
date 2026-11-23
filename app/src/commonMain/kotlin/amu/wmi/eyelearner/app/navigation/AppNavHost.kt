package amu.wmi.eyelearner.app.navigation

import amu.wmi.eyelearner.app.auth.presentation.login.LoginRoute
import amu.wmi.eyelearner.app.auth.presentation.profile.ProfileRoute
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Route = Route.Login,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Login> {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigate(Route.Profile) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Profile> {
            ProfileRoute(
                onLoggedOut = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Profile) { inclusive = true }
                    }
                }
            )
        }
    }
}