package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.in_app_states.DataTransferStatus
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations

fun NavGraphBuilder.authScreen(
    toRegisterScreen: () -> Unit,
    onOfflineUseClick: () -> Unit,
    sendLoginData: (String, String) -> Unit,
    transferState: DataTransferStatus
) {
    composable(Destinations.AUTH.route) {
        AuthScreen(
            sendLoginData = sendLoginData,
            transferState = transferState,
            onRegisterClick = toRegisterScreen,
            onOfflineUseClick = onOfflineUseClick
        )
    }
}

fun NavController.toAuthScreen() {
    navigate(route = Destinations.AUTH.route)
}
