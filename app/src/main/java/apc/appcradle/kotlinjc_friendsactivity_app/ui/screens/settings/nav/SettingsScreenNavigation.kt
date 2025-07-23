package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.SettingsScreen

fun NavGraphBuilder.settingsScreen(viewModel: MainViewModel) {
    composable(Destinations.SETTINGS.route) { SettingsScreen(viewModel) }
}

fun NavController.toSettingsScreen() {
    navigate(route = Destinations.SETTINGS.route)
}