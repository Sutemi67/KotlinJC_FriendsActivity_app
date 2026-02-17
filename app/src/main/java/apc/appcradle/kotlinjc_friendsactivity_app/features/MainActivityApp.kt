package apc.appcradle.kotlinjc_friendsactivity_app.features

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppBackgroundImage
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppBottomNavBar
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppTopBar
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.ServiceRestartingFunc
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.TrancateSnackBarManager
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.UiState
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav.authScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav.registerScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav.toAuthScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.nav.ratingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.data.SettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.settingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.splash.SplashScreenUi
import org.koin.compose.koinInject

@Composable
fun MainActivityApp(
    appStateManager: AppStateManager = koinInject(),
    settingsRepository: SettingsRepository = koinInject()
) {
    val settingsState = settingsRepository.settingsState.collectAsState()
    val uiState = appStateManager.uiState.collectAsState()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackHostState = remember { SnackbarHostState() }

    ServiceRestartingFunc(settingsState)
    TrancateSnackBarManager(appStateManager = appStateManager, snackbarHostState = snackHostState)

    LaunchedEffect(uiState.value) {
        when (uiState.value) {
            UiState.OFFLINE -> navController.toMainScreen()
            UiState.SPLASH -> navController.navigate(route = Destinations.SPLASH.route)
            UiState.LOGGED_OUT -> navController.toAuthScreen()
            UiState.LOGGED_IT -> navController.toMainScreen()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackHostState) },
        topBar = {
            val userLogin = settingsState.value.userLogin
            val isAuthScreen = uiState.value == UiState.LOGGED_OUT
            if (!isAuthScreen)
                AppTopBar(
                    login = userLogin,
                    screenRoute = navBackStackEntry?.destination?.route
                )
        },
        bottomBar = {
            val isAuthScreen = uiState.value == UiState.LOGGED_OUT

            if (!isAuthScreen) {
                val bottomDestinations: List<Destinations> by remember {
                    derivedStateOf {
                        if (settingsState.value.userLogin == null) {
                            Destinations.offlineDestinations
                        } else {
                            Destinations.noAuthDestinations
                        }
                    }
                }
                AppBottomNavBar(
                    navDestinations = bottomDestinations,
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it.route) }
                )
            }
        }
    ) { contentPadding ->
        Box {
            AppBackgroundImage()
            NavHost(
                modifier = Modifier.padding(contentPadding),
                navController = navController,
                startDestination = Destinations.SPLASH.route
            ) {
                composable(route = Destinations.SPLASH.route) { SplashScreenUi() }
                authScreen(toRegisterScreen = navController::toRegisterScreen)
                registerScreen(toMainScreen = navController::toMainScreen)
                mainScreen()
                ratingsScreen()
                settingsScreen()
            }
        }
    }
}

