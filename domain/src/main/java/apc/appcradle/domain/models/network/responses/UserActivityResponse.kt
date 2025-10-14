package apc.appcradle.domain.models.network.responses

import apc.appcradle.domain.models.network.requests.UserActivityRequest
import kotlinx.serialization.Serializable

@Serializable
data class UserActivityResponse(
    val friendsList: MutableList<UserActivityRequest>,
    val errorMessage: String?,
    val leader: String?
)