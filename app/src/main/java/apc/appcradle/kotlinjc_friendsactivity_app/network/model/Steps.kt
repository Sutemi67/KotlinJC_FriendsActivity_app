package apc.appcradle.kotlinjc_friendsactivity_app.network.model

import androidx.compose.runtime.Stable

@Stable
data class Steps(
    val weeklySteps: Int,
    val allSteps: Int
)