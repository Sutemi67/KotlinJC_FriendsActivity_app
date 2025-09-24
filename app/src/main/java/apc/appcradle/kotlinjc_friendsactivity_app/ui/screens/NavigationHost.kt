package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.data.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.data.SensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.authScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.registerScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav.ratingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav.settingsScreen
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
    val statsRepository = koinInject<StatsRepository>()

    val context = LocalContext.current
    val transferState = viewModel.transferState.collectAsState().value
    val steps = sensorManager.allSteps.collectAsState().value
    val weeklySteps = sensorManager.weeklySteps.collectAsState().value
    val isSynced = statsRepository.syncStatus.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.isServiceRunning(context)
        Log.d("sensors", sensorManager.isStepSensorAvailable.toString())
    }

    CompositionLocalProvider(
        LocalSensorManager provides sensorManager,
    ) {
        Scaffold(
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
                        noAuthDestinations.forEachIndexed { index, item ->
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
                    isSynced = isSynced,
                    syncFun = {
                        viewModel.syncData(
                            login = state.userLogin!!,
                            steps = steps,
                            weeklySteps = weeklySteps
                        )
                    }
                )
                settingsScreen(
                    onLogoutClick = { viewModel.logout() },
                    userLogin = state.userLogin,
                    userStepLength = state.userStepLength,
                    userScale = state.userScale,
                    onThemeClick = { viewModel.changeTheme(it) },
                    onNickNameClick = { login, newLogin -> viewModel.changeLogin(login, newLogin) },
                    onStepLengthClick = { viewModel.changeStepLength(it) },
                    currentTheme = state.currentTheme,
                    onScaleClick = { viewModel.changeScale(it) }
                )
            }
        }
    }
}

