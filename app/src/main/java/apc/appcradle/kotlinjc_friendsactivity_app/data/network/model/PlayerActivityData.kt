package apc.appcradle.kotlinjc_friendsactivity_app.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerActivityData(
    val login: String,
    val steps: Int,
    val weeklySteps: Int,
    val percentage: Float
)