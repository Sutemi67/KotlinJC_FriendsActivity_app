package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.MainUserScreen

fun NavController.toMainScreen() {
    navigate(route = Destinations.MAIN.route)
}

fun NavGraphBuilder.mainScreen(viewModel: MainViewModel) {
    composable(Destinations.MAIN.route) {
        MainUserScreen(
            viewModel = viewModel,
        )
    }
}