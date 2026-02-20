package apc.appcradle.kotlinjc_friendsactivity_app.features.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations

fun NavGraphBuilder.settingsScreen(
) {
    composable(Destinations.SETTINGS.route) {
        SettingsScreen()
    }
}

fun NavController.toSettingsScreen() {
    navigate(route = Destinations.SETTINGS.route)
}