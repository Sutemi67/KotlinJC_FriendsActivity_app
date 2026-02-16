package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.AuthViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.RegistrationScreen
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.DataTransferState
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.registerScreen(
    toMainScreen: () -> Unit
) {
    composable(Destinations.REGISTER.route) {
        RegistrationScreen(toMainScreen = toMainScreen,)
    }
}

fun NavController.toRegisterScreen() {
    navigate(route = Destinations.REGISTER.route)
}