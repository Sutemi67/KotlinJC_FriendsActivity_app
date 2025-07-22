package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.login.nav.toLoginScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav.toRatingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav.toSettingsScreen

enum class Destinations(
    val label: String,
    val iconUnselected: ImageVector,
    val iconSelected: ImageVector,
    val route: String,
    val navigateOnClick: (NavController) -> Unit,
) {
    LOGIN(
        "login",
        Icons.Filled.Lock,
        Icons.Filled.Home,
        "LoginScreenRoute",
        { it.toLoginScreen() }),
    REGISTER(
        "register",
        Icons.Filled.Lock,
        Icons.Filled.Home,
        "RegisterScreenRoute",
        { it.toRegisterScreen() }),
    MAIN(
        "main",
        Icons.Filled.Home,
        Icons.Filled.Home,
        "MainUserScreenRoute",
        { it.toMainScreen() }),
    RATINGS(
        "ratings",
        Icons.Filled.Settings,
        Icons.Filled.Home,
        "RatingsScreenRoute",
        { it.toRatingsScreen() }),
    SETTINGS(
        "settings",
        Icons.Filled.Settings,
        Icons.Filled.Home,
        "SettingsScreenRoute",
        { it.toSettingsScreen() })
}