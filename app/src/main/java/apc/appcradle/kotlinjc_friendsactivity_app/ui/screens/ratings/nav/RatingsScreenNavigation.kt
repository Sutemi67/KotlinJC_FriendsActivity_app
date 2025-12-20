package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.RatingsScreen

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