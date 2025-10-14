package apc.appcradle.domain.models.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token: String
)