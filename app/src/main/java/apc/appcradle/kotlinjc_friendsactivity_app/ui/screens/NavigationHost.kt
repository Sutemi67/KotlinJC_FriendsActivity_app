package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.SensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.authScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.registerScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav.ratingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav.settingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.utils.formatMillisecondsToDaysHoursMinutes
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

val LocalSensorManager =
    compositionLocalOf<SensorsManager> { error("No sensor manager provided") }

@Composable
fun NavigationHost(
    viewModel: MainViewModel,
    state: AppState
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val noAuthDestinations =
        if (state.userLogin == null) {
            Destinations.entries.filter { it != Destinations.AUTH && it != Destinations.REGISTER && it != Destinations.RATINGS }
        } else {
            Destinations.entries.filter { it != Destinations.AUTH && it != Destinations.REGISTER }
        }

    val sensorManager: SensorsManager = koinInject<SensorsManager>()
    val transferState = viewModel.transferState.collectAsState().value

    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.trancateWorkerStatus) {
        scope.launch {
            if (state.trancateWorkerStatus == null || state.trancateWorkerStatus.state.isFinished) {
                snackHostState.showSnackbar(message = "Планируется еженедельное обнуление...\nИзменения вступят в силу после перезапуска.")
            } else {
                when (state.trancateWorkerStatus.state) {
                    WorkInfo.State.ENQUEUED -> {
                        snackHostState.showSnackbar(
                            message = "Статус участия: Запланировано.\nПодведение итогов через: ${
                                formatMillisecondsToDaysHoursMinutes(
                                    state.trancateWorkerStatus.initialDelayMillis
                                )
                            }"
                        )
                    }

                    else -> {
                        snackHostState.showSnackbar(
                            message = "Статус участия: ${state.trancateWorkerStatus.state}\nПодведение итогов через: ${
                                formatMillisecondsToDaysHoursMinutes(
                                    state.trancateWorkerStatus.initialDelayMillis
                                )
                            }"
                        )
                    }

                }

            }
        }
    }

    CompositionLocalProvider(
        LocalSensorManager provides sensorManager,
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackHostState) },
            topBar = {
                if (navBackStackEntry?.destination?.route != Destinations.AUTH.route &&
                    navBackStackEntry?.destination?.route != Destinations.REGISTER.route
                )
                    AppComponents.AppTopBar(
                        login = state.userLogin,
                        screenRoute = navBackStackEntry?.destination?.route
                    )
            },
            bottomBar = {
                if (navBackStackEntry?.destination?.route != Destinations.AUTH.route &&
                    navBackStackEntry?.destination?.route != Destinations.REGISTER.route
                )
                    NavigationBar {
                        noAuthDestinations.forEach { item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (navBackStackEntry?.destination?.route == item.route) item.iconSelected else item.iconUnselected,
                                        contentDescription = stringResource(item.label),
                                    )
                                },
                                label = {
                                    AppComponents.AppText(
                                        stringResource(item.label),
                                        appTextStyle = AppTextStyles.Body
                                    )
                                },
                                selected = navBackStackEntry?.destination?.route == item.route,
                                onClick = { item.navigateOnClick(navController) },
                            )
                        }
                    }
            }
        ) { contentPadding ->
            val startDestination =
                if (state.isLoggedIn) Destinations.MAIN.route else Destinations.AUTH.route
            NavHost(
                modifier = Modifier.padding(contentPadding),
                navController = navController,
                startDestination = startDestination
            ) {
                authScreen(
                    toRegisterScreen = { navController.toRegisterScreen() },
                    transferState = transferState,
                    sendLoginData = { login, password -> viewModel.sendLoginData(login, password) },
                    onOfflineUseClick = { viewModel.goOfflineUse() }
                )
                registerScreen(
                    toMainScreen = { navController.toMainScreen() }
                )
                mainScreen(
                    viewModel = viewModel
                )
                ratingsScreen(
                    login = state.userLogin,
                    syncFun = {
                        val stepsNow = sensorManager.allSteps.value
                        val weeklyNow = sensorManager.weeklySteps.value
                        viewModel.syncData(
                            login = state.userLogin!!,
                            steps = stepsNow,
                            weeklySteps = weeklyNow
                        )
                    }
                )
                settingsScreen(
                    state = state,
                    onLogoutClick = { viewModel.logout() },
                    onThemeClick = { viewModel.changeTheme(it) },
                    onNickNameClick = { login, newLogin -> viewModel.changeLogin(login, newLogin) },
                    onStepLengthClick = { viewModel.changeStepLength(it) },
                    onScaleClick = { viewModel.changeScale(it) }
                )
            }
        }
    }
}

