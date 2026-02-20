package apc.appcradle.kotlinjc_friendsactivity_app.features

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import apc.appcradle.kotlinjc_friendsactivity_app.R

enum class Destinations(
    val label: Int,
    val iconUnselected: ImageVector,
    val iconSelected: ImageVector,
    val route: String,
) {
    AUTH(
        R.string.navigation_buttons_auth,
        Icons.Filled.Lock,
        Icons.Filled.Home,
        "AuthScreenRoute"
    ),
    REGISTER(
        R.string.navigation_buttons_register,
        Icons.Filled.Lock,
        Icons.Filled.Home,
        "RegisterScreenRoute"
    ),
    MAIN(
        R.string.navigation_buttons_home,
        Icons.Filled.Home,
        Icons.Filled.Home,
        "MainUserScreenRoute"
    ),
    RATINGS(
        R.string.navigation_buttons_ratings,
        Icons.Filled.DateRange,
        Icons.Filled.DateRange,
        "RatingsScreenRoute"
    ),
    SETTINGS(
        R.string.navigation_buttons_settings,
        Icons.Filled.Settings,
        Icons.Filled.Settings,
        "SettingsScreenRoute"
    ),
    SPLASH(
        R.string.navigation_buttons_settings,
        Icons.Filled.Settings,
        Icons.Filled.Settings,
        "SplashScreenRoute"
    );

    companion object
}

val Destinations.Companion.noAuthDestinations: List<Destinations>
    get() = Destinations.entries.filter { route ->
        route != Destinations.AUTH &&
                route != Destinations.REGISTER &&
                route != Destinations.SPLASH
    }

val Destinations.Companion.offlineDestinations: List<Destinations>
    get() = Destinations.entries.filter { route ->
        route != Destinations.AUTH &&
                route != Destinations.REGISTER &&
                route != Destinations.RATINGS &&
                route != Destinations.SPLASH
    }