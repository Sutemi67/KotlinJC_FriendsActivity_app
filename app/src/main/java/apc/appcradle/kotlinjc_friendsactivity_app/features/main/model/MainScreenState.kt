package apc.appcradle.kotlinjc_friendsactivity_app.features.main.model

import androidx.compose.runtime.Stable
import androidx.work.WorkInfo

@Stable
data class MainScreenState(

    //Login and Permissions
    val isPermissionsGet: Boolean = false,

    //UserData
    val userWeeklySteps: Int = 0,
    val userAllSteps: Int = 0,
    val isSensorsAvailable: Boolean = false,

    //LoadingState
    val isLoading: Boolean = false,

    //Trancate worker status
    val trancateWorkerStatus: WorkInfo? = null
)