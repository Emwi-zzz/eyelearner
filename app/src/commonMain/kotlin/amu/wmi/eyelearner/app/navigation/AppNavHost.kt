package amu.wmi.eyelearner.app.navigation

import amu.wmi.eyelearner.app.auth.presentation.login.LoginRoute
import amu.wmi.eyelearner.app.auth.presentation.profile.ProfileRoute
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import amu.wmi.eyelearner.app.navigation.NavigationData
import amu.wmi.eyelearner.app.auth.presentation.profile.HomeRoute

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

        val navData: NavigationData =
            NavigationData(
                onHome = { navController.navigate(Route.Home) },
                onSearchStory = { println("on search my story") },
                onMyStories = { println("On my stories") },
                onCalendar = { println("On calendar") },
                onProfile = { println("On profile") },
            )

        composable<Route.Login> {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeRoute(
                navData = navData,
                onLoggedOut = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Home) { inclusive = true }
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