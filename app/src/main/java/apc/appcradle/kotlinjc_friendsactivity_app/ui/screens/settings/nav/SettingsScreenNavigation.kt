package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.SettingsScreen

fun NavGraphBuilder.settingsScreen(
    userLogin: String?,
    userStepLength: Double,
    userScale: Float,
    onLogoutClick: () -> Unit,
    onThemeClick: (AppThemes) -> Unit,
    onStepLengthClick: (Double) -> Unit,
    onNickNameClick: () -> Unit,
    onScaleClick: (Float) -> Unit,
    currentTheme: AppThemes
) {
    composable(Destinations.SETTINGS.route) {
        SettingsScreen(
            userLogin = userLogin,
            userStepLength = userStepLength,
            userScale = userScale,
            onLogoutClick = onLogoutClick,
            onThemeClick = onThemeClick,
            onStepDistanceClick = onStepLengthClick,
            onNicknameClick = onNickNameClick,
            currentTheme = currentTheme,
            onScaleClick = onScaleClick
        )
    }
}

fun NavController.toSettingsScreen() {
    navigate(route = Destinations.SETTINGS.route)
}