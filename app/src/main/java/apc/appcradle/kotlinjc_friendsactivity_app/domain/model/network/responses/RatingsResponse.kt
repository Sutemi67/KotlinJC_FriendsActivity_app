package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.RatingsRequest
import kotlinx.serialization.Serializable

@Serializable
data class RatingsResponse(
    val friendsList: MutableList<RatingsRequest>,
    val errorMessage: String?,
    val leader: String?
)
