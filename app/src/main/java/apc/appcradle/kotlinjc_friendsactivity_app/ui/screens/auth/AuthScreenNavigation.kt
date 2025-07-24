package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations

fun NavGraphBuilder.authScreen(
    viewModel: MainViewModel,
    toRegisterScreen: () -> Unit
) {
    composable(Destinations.AUTH.route) {
        AuthScreen(
            viewModel = viewModel,
            onRegisterClick = toRegisterScreen
        )
    }
}

fun NavController.toAuthScreen() {
    navigate(route = Destinations.AUTH.route)
}
