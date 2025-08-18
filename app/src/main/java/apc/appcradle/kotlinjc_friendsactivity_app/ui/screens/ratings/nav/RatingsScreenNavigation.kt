package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.RatingsScreen

fun NavController.toRatingsScreen() {
    navigate(route = Destinations.RATINGS.route)
}

fun NavGraphBuilder.ratingsScreen(
    login: String?,
    stepCount: Int,
    isSynced: Boolean,
    syncFun: suspend (String, Int) -> PlayersListSyncData
) {
    composable(Destinations.RATINGS.route) {
        RatingsScreen(
            login = login,
            stepCount = stepCount,
            isSynced = isSynced,
            syncFun = syncFun
        )
    }
}