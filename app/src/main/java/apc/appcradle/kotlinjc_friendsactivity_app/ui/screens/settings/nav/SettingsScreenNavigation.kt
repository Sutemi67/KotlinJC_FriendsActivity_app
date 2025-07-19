package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.SettingsScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.settingsScreen() {
    composable(Destinations.SETTINGS.route) { SettingsScreen() }
}

fun NavController.toSettingsScreen() {
    navigate(route = Destinations.SETTINGS.route)
}