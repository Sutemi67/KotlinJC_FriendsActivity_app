package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations

data class AppState(

    //Login and Permissions
    val isPermissionsGet: Boolean = false,
    val isServiceRunning: Boolean = false,
    val isLoggedIn: Boolean = false,

    //Navigation
    val currentDestination: String = Destinations.AUTH.route,

    //Settings
    val userLogin: String? = null,
    val userStepLength: Double? = null,
    val currentTheme: AppThemes = AppThemes.System,
    val settingsClass: AppSavedSettingsData = AppSavedSettingsData()
)
