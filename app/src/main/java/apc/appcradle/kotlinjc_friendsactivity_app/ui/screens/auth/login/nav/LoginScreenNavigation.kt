package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.login.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.login.LoginScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.loginScreen(toMainScreen: () -> Unit, onRegisterClick: () -> Unit) {
    composable(Destinations.LOGIN.route) {
        LoginScreen(
            toMainScreen = toMainScreen,
            onRegisterClick = onRegisterClick
        )
    }
}

fun NavController.toLoginScreen() {
    navigate(route = Destinations.LOGIN.route)
}
