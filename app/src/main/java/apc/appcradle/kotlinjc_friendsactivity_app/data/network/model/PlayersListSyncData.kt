package apc.appcradle.kotlinjc_friendsactivity_app.data.network.model

data class PlayersListSyncData(
    val playersList: List<PlayerActivityData> = emptyList(),
    val summaryKm: Double = 0.0,
    val leaderDifferenceKm: Double = 0.0,
    val errorMessage: String? = null,
    val leader: String? = null
)