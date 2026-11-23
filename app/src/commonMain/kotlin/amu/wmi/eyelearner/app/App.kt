package amu.wmi.eyelearner.app

import amu.wmi.eyelearner.app.core.di.appModule
import amu.wmi.eyelearner.app.navigation.AppNavHost
import amu.wmi.eyelearner.app.navigation.Route
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration


@Composable
internal fun App(
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    KoinApplication(configuration = koinConfiguration { modules(appModule) }) {
        val navController = rememberNavController()
        AppNavHost(
            navController = navController
        )
        LaunchedEffect(navController) {
            onNavHostReady(navController)
        }
    }
}