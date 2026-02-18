package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.LoginScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations

fun NavGraphBuilder.authScreen(
    toRegisterScreen: () -> Unit,
) {
    composable(Destinations.AUTH.route) {
        LoginScreen(
            navigateToRegister = toRegisterScreen,
        )
    }
}

fun NavController.toAuthScreen() {
    navigate(route = Destinations.AUTH.route)
}
