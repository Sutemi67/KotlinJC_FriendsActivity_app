package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerActivityData(
    val login: String,
    val steps: Int,
    var percentage: Float
)
