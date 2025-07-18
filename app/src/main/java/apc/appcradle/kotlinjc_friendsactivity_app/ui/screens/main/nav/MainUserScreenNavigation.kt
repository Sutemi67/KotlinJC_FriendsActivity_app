package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.MainUserScreen
import kotlinx.serialization.Serializable

@Serializable
object MainUserScreenRoute

fun NavController.toMainScreen() {
    navigate(route = MainUserScreenRoute)
}

fun NavGraphBuilder.mainScreen(
    viewModel: MainViewModel
) {
    composable<MainUserScreenRoute> {
        MainUserScreen(
            viewModel = viewModel
        )
    }
}