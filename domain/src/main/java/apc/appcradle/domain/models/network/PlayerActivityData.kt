package apc.appcradle.domain.models.network

import kotlinx.serialization.Serializable

@Serializable
data class PlayerActivityData(
    val login: String,
    val steps: Int,
    val weeklySteps: Int,
    var percentage: Float
)