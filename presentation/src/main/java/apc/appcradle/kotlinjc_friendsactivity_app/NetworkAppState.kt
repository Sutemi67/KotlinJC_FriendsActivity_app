package apc.appcradle.kotlinjc_friendsactivity_app

import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations

data class NetworkAppState(

    //Login and Permissions
//    val isPermissionsGet: Boolean = false,
//    val isServiceRunning: Boolean = false,
//    val isServiceEnabled: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userLogin: String? = null,

    //Navigation
    val currentDestination: String = Destinations.AUTH.route,

    //LoadingState
    val isLoading: Boolean = false,

//    //Settings
//    val userStepLength: Double = USER_STEP_DEFAULT,
//    val currentTheme: AppThemes = AppThemes.System,
//    val userScale: Float = 1.0f,

    //UserData
    val userWeeklySteps: Int = 0,
    val userAllSteps: Int = 0,
    val userChampionCount: Int = 0,

)