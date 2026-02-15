package apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.RatingsScreen

fun NavController.toRatingsScreen() {
    navigate(route = Destinations.RATINGS.route)
}

fun NavGraphBuilder.ratingsScreen(
    login: String?,
    syncFun: suspend () -> PlayersListSyncData
) {
    composable(Destinations.RATINGS.route) {
        RatingsScreen(
            login = login,
            syncFun = syncFun
        )
    }
}