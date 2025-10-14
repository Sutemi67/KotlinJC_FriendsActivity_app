package apc.appcradle.domain.models.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserActivityRequest(
    val login: String,
    val steps: Int,
    val weeklySteps: Int
)