package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.SettingsScreen

fun NavGraphBuilder.settingsScreen(
    userLogin: String?,
    onLogoutClick: () -> Unit,
    onThemeClick: () -> Unit,
    onStepDistanceClick: () -> Unit,
    onNickNameClick: () -> Unit,
) {
    composable(Destinations.SETTINGS.route) {
        SettingsScreen(
            userLogin = userLogin,
            onLogoutClick = onLogoutClick,
            onThemeClick = onThemeClick,
            onStepDistanceClick = onStepDistanceClick,
            onNicknameClick = onNickNameClick
        )
    }
}

fun NavController.toSettingsScreen() {
    navigate(route = Destinations.SETTINGS.route)
}