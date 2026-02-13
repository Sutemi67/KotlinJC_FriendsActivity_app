package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

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
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppBackgroundImage
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppBottomNavBar
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppTopBar
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.authScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.registerScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav.ratingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav.settingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.splash.SplashScreenUi
import apc.appcradle.kotlinjc_friendsactivity_app.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.utils.formatDeadline
import apc.appcradle.kotlinjc_friendsactivity_app.utils.logger
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

val LocalSensorManager =
    compositionLocalOf<AppSensorsManager> { error("No sensor manager provided") }

@Composable
fun NavigationHost() {
    val viewModel: MainViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value
    val authState = viewModel.authState.collectAsState().value
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isAuthScreen =
        currentRoute == Destinations.AUTH.route || currentRoute == Destinations.REGISTER.route

    val bottomDestinations: List<Destinations> = remember(authState.userLogin) {
        if (authState.userLogin == null) {
            Destinations.offlineDestinations
        } else {
            Destinations.noAuthDestinations
        }
    }

    val sensorManager: AppSensorsManager = koinInject<AppSensorsManager>()
    val transferState = viewModel.transferState.collectAsState().value

    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.trancateWorkerStatus) {
        scope.launch {
            if (authState.userLogin == null) return@launch
            if (state.trancateWorkerStatus == null || state.trancateWorkerStatus.state.isFinished) {
                snackHostState.showSnackbar(message = "Планируется еженедельное обнуление...\nИзменения вступят в силу после перезапуска.")
            } else {
                when (state.trancateWorkerStatus.state) {
                    WorkInfo.State.ENQUEUED -> {
                        snackHostState.showSnackbar(
                            message = "Статус обнуления: Запланировано.\nПодведение итогов через: ${
                                formatDeadline(
                                    state.trancateWorkerStatus.nextScheduleTimeMillis
                                )
                            }"
                        )
                    }

                    else -> {
                        snackHostState.showSnackbar(
                            message = "Статус обнуления: ${state.trancateWorkerStatus.state}\nПодведение итогов через: ${
                                formatDeadline(
                                    state.trancateWorkerStatus.nextScheduleTimeMillis
                                )
                            }"
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(authState.userLogin) { viewModel.refreshSteps() }

    CompositionLocalProvider(
        LocalSensorManager provides sensorManager,
    ) {
        logger(
            LoggerType.Info,
            "nav host recomposed inside -> ${authState.userLogin}, ${authState.isLoggedIn}"
        )
        Scaffold(
            snackbarHost = { SnackbarHost(snackHostState) },
            topBar = {
                if (!isAuthScreen)
                    AppTopBar(
                        login = authState.userLogin,
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
            val startDestination =
                if (state.isAppReady && authState.isLoggedIn) {
                    Destinations.MAIN.route
                } else if (state.isAppReady) {
                    Destinations.AUTH.route
                } else {
                    Destinations.SPLASH.route
                }
            Box {
                AppBackgroundImage()
                NavHost(
                    modifier = Modifier.padding(contentPadding),
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(route = Destinations.SPLASH.route) {
                        SplashScreenUi()
                    }
                    authScreen(
                        toRegisterScreen = navController::toRegisterScreen,
                        transferState = transferState,
                        sendLoginData = { login, password ->
                            viewModel.sendLoginData(
                                login,
                                password
                            )
                        },
                        onOfflineUseClick = viewModel::goOfflineUse
                    )
                    registerScreen(
                        viewModel = viewModel,
                        transferState = transferState,
                        toMainScreen = navController::toMainScreen
                    )
                    mainScreen(viewModel)
                    ratingsScreen(
                        login = authState.userLogin,
                        syncFun = {
                            val stepsNow = sensorManager.allSteps.value
                            val weeklyNow = sensorManager.weeklySteps.value
                            viewModel.syncData(
                                login = authState.userLogin!!,
                                steps = stepsNow,
                                weeklySteps = weeklyNow
                            )
                        }
                    )
                    settingsScreen(
                        state = state,
                        onLogoutClick = viewModel::logout,
                        onThemeClick = { viewModel.changeTheme(it) },
                        onNickNameClick = { login, newLogin ->
                            viewModel.changeLogin(
                                login,
                                newLogin
                            )
                        },
                        onStepLengthClick = { viewModel.changeStepLength(it) },
                        onScaleClick = { viewModel.changeScale(it) }
                    )
                }
            }
        }
    }
}

