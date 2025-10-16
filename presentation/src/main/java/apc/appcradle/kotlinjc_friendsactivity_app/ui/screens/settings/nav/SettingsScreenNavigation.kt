package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav

import androidx.compose.runtime.State
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.domain.models.AppThemes
import apc.appcradle.domain.models.local_data.SharedPreferencesData
import apc.appcradle.kotlinjc_friendsactivity_app.NetworkAppState
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.SettingsScreen

fun NavGraphBuilder.settingsScreen(
    settingsState: State<SharedPreferencesData>,
    networkState: State<NetworkAppState>,
    onLogoutClick: () -> Unit,
    onThemeClick: (AppThemes) -> Unit,
    onStepLengthClick: (Double) -> Unit,
    onNickNameClick: (String,String) -> Unit,
    onScaleClick: (Float) -> Unit,
) {
    composable(Destinations.SETTINGS.route) {
        SettingsScreen(
            settingsState = settingsState,
            networkState = networkState,
            onLogoutClick = onLogoutClick,
            onThemeClick = onThemeClick,
            onStepDistanceClick = onStepLengthClick,
            onNicknameClick = onNickNameClick,
            onScaleClick = onScaleClick
        )
    }
}

fun NavController.toSettingsScreen() {
    navigate(route = Destinations.SETTINGS.route)
}