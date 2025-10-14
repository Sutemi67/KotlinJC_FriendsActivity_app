package apc.appcradle.domain.models.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginChangeRequest(
    val login: String,
    val newLogin: String
)
