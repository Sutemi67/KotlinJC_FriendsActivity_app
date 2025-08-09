package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.SettingsScreen

fun NavGraphBuilder.settingsScreen(
    userLogin: String?,
    userStepLength: Double? = 0.4,
    onLogoutClick: () -> Unit,
    onThemeClick: (AppThemes) -> Unit,
    onStepDistanceClick: () -> Unit,
    onNickNameClick: () -> Unit,
    currentTheme: AppThemes
) {
    composable(Destinations.SETTINGS.route) {
        SettingsScreen(
            userLogin = userLogin,
            userStepLength = userStepLength,
            onLogoutClick = onLogoutClick,
            onThemeClick = onThemeClick,
            onStepDistanceClick = onStepDistanceClick,
            onNicknameClick = onNickNameClick,
            currentTheme = currentTheme
        )
    }
}

fun NavController.toSettingsScreen() {
    navigate(route = Destinations.SETTINGS.route)
}