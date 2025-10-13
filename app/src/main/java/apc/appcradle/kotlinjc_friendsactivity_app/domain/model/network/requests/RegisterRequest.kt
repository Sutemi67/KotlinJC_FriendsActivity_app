package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String
)