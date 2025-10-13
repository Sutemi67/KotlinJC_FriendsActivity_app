package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.UserActivityRequest
import kotlinx.serialization.Serializable

@Serializable
data class UserActivityResponse(
    val friendsList: MutableList<UserActivityRequest>,
    val errorMessage: String?,
    val leader: String?
)