package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav

import androidx.compose.runtime.State
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.NetworkAppState
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.RegistrationScreen

fun NavGraphBuilder.registerScreen(
    toMainScreen: () -> Unit,
    sendRegisterCallback: (String, String) -> Unit,
    networkState: State<NetworkAppState>
) {
    composable(Destinations.REGISTER.route) {
        RegistrationScreen(
            networkResult = networkState,
            toMainScreen = toMainScreen,
            sendRegisterCallback = sendRegisterCallback
        )
    }
}

fun NavController.toRegisterScreen() {
    navigate(route = Destinations.REGISTER.route)
}