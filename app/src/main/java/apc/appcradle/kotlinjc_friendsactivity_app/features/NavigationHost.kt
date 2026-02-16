package apc.appcradle.kotlinjc_friendsactivity_app.features

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.formatDeadline
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppBackgroundImage
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppBottomNavBar
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppTopBar
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.UiState
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav.authScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav.registerScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav.toAuthScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.nav.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.nav.ratingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.nav.settingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.features.splash.SplashScreenUi
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

val LocalSensorManager =
    compositionLocalOf<AppSensorsManager> { error("No sensor manager provided") }

@Composable
fun NavigationHost(
    appStateManager: AppStateManager = koinInject()
) {
    val appState = appStateManager.appState.collectAsState().value
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isAuthScreen = appState.uiState == UiState.LOGGED_OUT

    val bottomDestinations: List<Destinations> = remember(appState.userLogin) {
        if (appState.userLogin == null) {
            Destinations.offlineDestinations
        } else {
            Destinations.noAuthDestinations
        }
    }

    val sensorManager: AppSensorsManager = koinInject<AppSensorsManager>()

    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(appState.trancateWorkerStatus, appState.userLogin) {
        scope.launch {
            if (appState.userLogin == null) return@launch
            if (appState.trancateWorkerStatus == null || appState.trancateWorkerStatus.state.isFinished) {
                snackHostState.showSnackbar(message = "Планируется еженедельное обнуление...\nИзменения вступят в силу после перезапуска.")
            } else {
                when (appState.trancateWorkerStatus.state) {
                    WorkInfo.State.ENQUEUED -> {
                        snackHostState.showSnackbar(
                            message = "Статус обнуления: Запланировано.\nПодведение итогов через: ${
                                formatDeadline(
                                    appState.trancateWorkerStatus.nextScheduleTimeMillis
                                )
                            }"
                        )
                    }

                    else -> {
                        snackHostState.showSnackbar(
                            message = "Статус обнуления: ${appState.trancateWorkerStatus.state}\nПодведение итогов через: ${
                                formatDeadline(
                                    appState.trancateWorkerStatus.nextScheduleTimeMillis
                                )
                            }"
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(appState.uiState) {
        when (appState.uiState) {
            UiState.OFFLINE -> navController.toMainScreen()
            UiState.SPLASH -> navController.navigate(route = Destinations.SPLASH.route)
            UiState.LOGGED_OUT -> navController.toAuthScreen()
            UiState.LOGGED_IT -> navController.toMainScreen()
        }
    }
    CompositionLocalProvider(
        LocalSensorManager provides sensorManager,
    ) {
        logger(
            LoggerType.Error,
            "nav host recomposed inside -> ${appState.userLogin}"
        )
        Scaffold(
            snackbarHost = { SnackbarHost(snackHostState) },
            topBar = {
                if (!isAuthScreen)
                    AppTopBar(
                        login = appState.userLogin,
                        screenRoute = navBackStackEntry?.destination?.route
                    )
            },
            bottomBar = {
                if (!isAuthScreen)
                    AppBottomNavBar(
                        navDestinations = bottomDestinations,
                        currentRoute = currentRoute,
                        onNavigate = { navController.navigate(it.route) }
                    )
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
}

