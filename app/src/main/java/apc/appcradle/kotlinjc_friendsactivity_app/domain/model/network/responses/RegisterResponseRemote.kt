package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseRemote(
    val token: String
)