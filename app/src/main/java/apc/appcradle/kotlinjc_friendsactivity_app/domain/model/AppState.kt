package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

import androidx.work.WorkInfo
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.utils.USER_STEP_DEFAULT

data class AppState(

    //Login and Permissions
    val isPermissionsGet: Boolean = false,
    val isServiceRunning: Boolean = false,
    val isServiceEnabledByUser: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userLogin: String? = null,

    //Navigation
    val currentDestination: String = Destinations.AUTH.route,

    //Settings
    val userStepLength: Double = USER_STEP_DEFAULT,
    val currentTheme: AppThemes = AppThemes.System,
    val userScale: Float = 1.0f,

    //UserData
    val userWeeklySteps: Int = 0,
    val userAllSteps: Int = 0,
    val userChampionCount: Int = 0,

    //LoadingState
    val isLoading: Boolean = false,

    //Trancate worker status
    val trancateWorkerStatus: WorkInfo? = null
)