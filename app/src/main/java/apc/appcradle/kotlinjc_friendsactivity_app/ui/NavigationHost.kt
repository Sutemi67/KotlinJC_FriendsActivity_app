package apc.appcradle.kotlinjc_friendsactivity_app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.login.nav.LoginScreenRoute
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.login.nav.loginScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationHost(
) {
    val viewModel: MainViewModel = koinViewModel()
    val navController = rememberNavController()
    val state = viewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = LoginScreenRoute
    ) {
        loginScreen(
            toMainScreen = { navController.toMainScreen() }
        )
        mainScreen(
            viewModel = viewModel
        )
    }
}