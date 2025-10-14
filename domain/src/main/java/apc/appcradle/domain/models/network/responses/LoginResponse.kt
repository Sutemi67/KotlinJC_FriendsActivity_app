package apc.appcradle.domain.models.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)