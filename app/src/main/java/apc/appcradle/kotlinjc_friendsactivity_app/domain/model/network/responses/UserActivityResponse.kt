package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.UserActivity
import kotlinx.serialization.Serializable

@Serializable
data class UserActivityResponse(
    val friendsList: MutableList<UserActivity>,
    val errorMessage: String?,
    val leader: String?
)