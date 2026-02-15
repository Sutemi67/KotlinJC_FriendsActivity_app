package apc.appcradle.kotlinjc_friendsactivity_app.network.model

import kotlinx.serialization.Serializable

sealed interface Requests {
    @Serializable
    data class LoginChangeRequest(
        val login: String,
        val newLogin: String
    ) : Requests

    @Serializable
    data class LoginRequest(
        val login: String,
        val password: String
    ) : Requests

    @Serializable
    data class RegisterRequest(
        val login: String,
        val password: String
    ) : Requests

    @Serializable
    data class UserActivityRequest(
        val login: String,
        val steps: Int,
        val weeklySteps: Int
    ) : Requests
}