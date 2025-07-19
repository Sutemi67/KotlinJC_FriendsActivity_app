package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.login.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginScreenRoute

fun NavGraphBuilder.loginScreen(toMainScreen: () -> Unit) {
    composable(Destinations.LOGIN.route) { LoginScreen(toMainScreen = toMainScreen) }
}

fun NavController.toLoginScreen() {
    navigate(route = Destinations.LOGIN.route)
}
