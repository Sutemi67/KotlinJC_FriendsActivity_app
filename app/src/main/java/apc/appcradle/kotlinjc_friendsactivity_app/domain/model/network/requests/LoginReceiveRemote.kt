package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginReceiveRemote(
    val login: String,
    val password: String
)