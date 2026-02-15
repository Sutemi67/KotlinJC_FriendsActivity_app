package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.AuthScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations

fun NavGraphBuilder.authScreen(
    toRegisterScreen: () -> Unit,
    onOfflineUseClick: () -> Unit,
    sendLoginData: (String, String) -> Unit,
    transferState: DataTransferState
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
