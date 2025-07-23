package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations

data class AppState(
    val isPermissionsGet: Boolean = false,
    val currentDestination: String = Destinations.LOGIN.route,
    val isServiceRunning: Boolean = false,
    val isLoggedIn: Boolean = false
)
