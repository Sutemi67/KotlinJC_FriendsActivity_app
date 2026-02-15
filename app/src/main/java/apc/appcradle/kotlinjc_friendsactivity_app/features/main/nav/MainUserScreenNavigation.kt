package apc.appcradle.kotlinjc_friendsactivity_app.features.main.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.MainUserScreen

fun NavController.toMainScreen() {
    navigate(route = Destinations.MAIN.route) {
        popUpTo(Destinations.MAIN.route) {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.mainScreen(vm: MainViewModel) {
    composable(Destinations.MAIN.route) {
        MainUserScreen(vm = vm)
    }
}