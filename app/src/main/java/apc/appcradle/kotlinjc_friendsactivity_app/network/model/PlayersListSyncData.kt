package apc.appcradle.kotlinjc_friendsactivity_app.network.model

import androidx.compose.runtime.Stable

@Stable
data class PlayersListSyncData(
    val playersList: List<PlayerActivityData> = emptyList(),
    val summaryKm: Double = 0.0,
    val leaderDifferenceKm: Double = 0.0,
    val errorMessage: String? = null,
    val leader: String? = null
)