package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.SettingsScreen

fun NavGraphBuilder.settingsScreen(
    onLogoutClick: () -> Unit,
    onThemeClick: (AppThemes) -> Unit,
    onStepLengthClick: (Double) -> Unit,
    onNickNameClick: (String, String) -> Unit,
    onScaleClick: (Float) -> Unit,
    state: AppState,
) {
    composable(Destinations.SETTINGS.route) {
        SettingsScreen(
            onLogoutClick = onLogoutClick,
            onThemeClick = onThemeClick,
            onStepDistanceClick = onStepLengthClick,
            onNicknameClick = onNickNameClick,
            onScaleClick = onScaleClick,
            state = state,
        )
    }
}

fun NavController.toSettingsScreen() {
    navigate(route = Destinations.SETTINGS.route)
}