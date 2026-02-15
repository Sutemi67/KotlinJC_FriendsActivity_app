package apc.appcradle.kotlinjc_friendsactivity_app.features.main.model

import androidx.compose.runtime.Stable
import androidx.work.WorkInfo

@Stable
data class UserData(
    val allSteps: Int,
    val weeklySteps: Int,
    val trancateWorkerStatus: WorkInfo?
)
