package apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models

import androidx.compose.runtime.Stable
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseState
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.PlayerActivityData

@Stable
data class RatingsState(
    val isLoading: Boolean = false,
    val userLogin: String? = null,
    val playersList: List<PlayerActivityData> = emptyList(),
    val summaryKm: Double = 0.0,
    val leaderDifferenceKm: Double = 0.0,
    val leader: String? = null,
    val errorMessage: String? = null
) : BaseState