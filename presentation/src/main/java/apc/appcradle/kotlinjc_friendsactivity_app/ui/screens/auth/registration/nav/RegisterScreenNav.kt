package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.RegistrationScreen
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.registerScreen(
    toMainScreen: () -> Unit
) {
    composable(Destinations.REGISTER.route) {
        RegistrationRoute(
            toMainScreen = toMainScreen,
        )
    }
}

@Composable
fun RegistrationRoute(
    toMainScreen: () -> Unit,
) {
    val viewModel: MainViewModel = koinViewModel()
    val transferResult by viewModel.transferState.collectAsState()
    val sendRegisterCallback = remember(viewModel) {
        { login: String, password: String ->
            viewModel.sendRegisterData(
                login,
                password
            )
        }
    }

    RegistrationScreen(
        transferResult = transferResult,
        toMainScreen = toMainScreen,
        sendRegisterCallback = sendRegisterCallback
    )
}

fun NavController.toRegisterScreen() {
    navigate(route = Destinations.REGISTER.route)
}