package apc.appcradle.domain.models.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String
)