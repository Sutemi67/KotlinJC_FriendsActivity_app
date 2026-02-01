package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.Requests
import kotlinx.serialization.Serializable

sealed interface Responses {
    @Serializable
    data class LoginResponse(
        val token: String
    ) : Responses

    @Serializable
    data class RegisterResponse(
        val token: String
    ) : Responses

    @Serializable
    data class UserActivityResponse(
        val friendsList: MutableList<Requests.UserActivityRequest>,
        val errorMessage: String?,
        val leader: String?
    ) : Responses

    @Serializable
    data class UserDataResponse(
        val steps: Int? = null,
        val weeklySteps: Int? = null,
        val errorMessage: String? = null
    ) : Responses
}