package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network

import kotlinx.serialization.Serializable

@Serializable
data class PlayerActivityData(
    val login: String,
    val steps: Int,
    val weeklySteps: Int,
    var percentage: Float
)