package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav

import androidx.compose.runtime.State
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.domain.models.network.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.NetworkAppState
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.RatingsScreen

fun NavController.toRatingsScreen() {
    navigate(route = Destinations.RATINGS.route)
}

fun NavGraphBuilder.ratingsScreen(
    networkState: State<NetworkAppState>,
    syncFun: suspend () -> PlayersListSyncData
) {
    composable(Destinations.RATINGS.route) {
        RatingsScreen(
            networkState = networkState,
            syncFun = syncFun
        )
    }
}