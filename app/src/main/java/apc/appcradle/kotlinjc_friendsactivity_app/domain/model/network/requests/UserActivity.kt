package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserActivity(
    val login: String,
    val steps: Int,
    val weeklySteps: Int
)