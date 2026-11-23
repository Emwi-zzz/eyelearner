package amu.wmi.eyelearner.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Login : Route
    @Serializable
    data object Profile : Route
}
