package apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models

import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseState
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.PlayersListSyncData

data class RatingsState(
    val isLoading: Boolean = false,
    val userLogin: String? = null,

    val list: PlayersListSyncData = PlayersListSyncData()
) : BaseState