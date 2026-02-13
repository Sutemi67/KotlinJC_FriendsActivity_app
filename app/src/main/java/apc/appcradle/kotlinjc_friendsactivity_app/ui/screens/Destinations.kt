package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.toAuthScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav.toRatingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav.toSettingsScreen

enum class Destinations(
    val label: Int,
    val iconUnselected: ImageVector,
    val iconSelected: ImageVector,
    val route: String,
    val navigateOnClick: (NavController) -> Unit,
) {
    AUTH(
        R.string.navigation_buttons_auth,
        Icons.Filled.Lock,
        Icons.Filled.Home,
        "AuthScreenRoute",
        { it.toAuthScreen() }),
    REGISTER(
        R.string.navigation_buttons_register,
        Icons.Filled.Lock,
        Icons.Filled.Home,
        "RegisterScreenRoute",
        { it.toRegisterScreen() }),
    MAIN(
        R.string.navigation_buttons_home,
        Icons.Filled.Home,
        Icons.Filled.Home,
        "MainUserScreenRoute",
        { it.toMainScreen() }),
    RATINGS(
        R.string.navigation_buttons_ratings,
        Icons.Filled.DateRange,
        Icons.Filled.DateRange,
        "RatingsScreenRoute",
        { it.toRatingsScreen() }),
    SETTINGS(
        R.string.navigation_buttons_settings,
        Icons.Filled.Settings,
        Icons.Filled.Settings,
        "SettingsScreenRoute",
        { it.toSettingsScreen() }),
    SPLASH(
        R.string.navigation_buttons_settings,
        Icons.Filled.Settings,
        Icons.Filled.Settings,
        "SplashScreenRoute",
        { it.toSettingsScreen() });

    companion object
}

val Destinations.Companion.noAuthDestinations: List<Destinations>
    get() = Destinations.entries.filter { route ->
        route != Destinations.AUTH && route != Destinations.REGISTER && route != Destinations.SPLASH
    }
val Destinations.Companion.offlineDestinations: List<Destinations>
    get() = Destinations.entries.filter { route ->
        route != Destinations.AUTH &&
                route != Destinations.REGISTER &&
                route != Destinations.RATINGS &&
                route != Destinations.SPLASH
    }