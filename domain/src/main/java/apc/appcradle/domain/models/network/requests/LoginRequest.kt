package apc.appcradle.domain.models.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)