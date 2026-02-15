package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.RegistrationScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations

fun NavGraphBuilder.registerScreen(
    viewModel: MainViewModel,
    transferState: DataTransferState,
    toMainScreen: () -> Unit
) {
    composable(Destinations.REGISTER.route) {
        RegistrationRoute(
            viewModel = viewModel,
            transferState = transferState,
            toMainScreen = toMainScreen,
        )
    }
}

@Composable
fun RegistrationRoute(
    viewModel: MainViewModel,
    transferState: DataTransferState,
    toMainScreen: () -> Unit,
) {
    val sendRegisterCallback = remember(viewModel) {
        { login: String, password: String ->
            viewModel.sendRegisterData(
                login,
                password
            )
        }
    }

    RegistrationScreen(
        transferResult = transferState,
        toMainScreen = toMainScreen,
        sendRegisterCallback = sendRegisterCallback
    )
}

fun NavController.toRegisterScreen() {
    navigate(route = Destinations.REGISTER.route)
}