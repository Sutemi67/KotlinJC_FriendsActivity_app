package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.RegistrationScreen

fun NavGraphBuilder.registerScreen(viewModel: MainViewModel,navController: NavController) {
    composable(Destinations.REGISTER.route) {
        RegistrationScreen(viewModel = viewModel, navController = navController)
    }
}

fun NavController.toRegisterScreen() {
    navigate(route = Destinations.REGISTER.route)
}