package apc.appcradle.kotlinjc_friendsactivity_app.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.NetworkViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.ServiceViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.SettingsViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.authScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.registerScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav.ratingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav.settingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.AppTextStyles
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationHost(
    networkViewModel: NetworkViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    serviceViewModel: ServiceViewModel = koinViewModel()
) {
    val networkState = networkViewModel.networkState.collectAsState()
    val stepsDataState by serviceViewModel.stepsDataState.collectAsState()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val noAuthDestinations =
        if (networkState.value.userLogin == null) {
            Destinations.entries.filter { it != Destinations.AUTH && it != Destinations.REGISTER && it != Destinations.RATINGS }
        } else {
            Destinations.entries.filter { it != Destinations.AUTH && it != Destinations.REGISTER }
        }

    val transferState = networkViewModel.transferState.collectAsState().value

    LaunchedEffect(Unit) {
        serviceViewModel.updateServiceState()
    }
    Scaffold(
        topBar = {
            if (navBackStackEntry?.destination?.route != Destinations.AUTH.route &&
                navBackStackEntry?.destination?.route != Destinations.REGISTER.route
            )
                AppComponents.AppTopBar(
                    login = networkState.value.userLogin,
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
            if (networkState.value.isLoggedIn) Destinations.MAIN.route else Destinations.AUTH.route
        NavHost(
            modifier = Modifier.padding(contentPadding),
            navController = navController,
            startDestination = startDestination
        ) {
            authScreen(
                toRegisterScreen = { navController.toRegisterScreen() },
                transferState = transferState,
                sendLoginData = { login, password ->
                    networkViewModel.sendLoginData(
                        login,
                        password
                    )
                },
                onOfflineUseClick = { networkViewModel.goOfflineUse() }
            )
            registerScreen(
                toMainScreen = { navController.toMainScreen() }
            )
            mainScreen(
                serviceViewModel = serviceViewModel
            )
            ratingsScreen(
                login = networkState.value.userLogin,
                isSynced = transferState.isLoading,
                syncFun = {
                    val stepsNow = stepsDataState.userAllSteps
                    val weeklyNow = stepsDataState.userWeeklySteps
                    networkViewModel.syncData(
                        login = networkState.value.userLogin!!,
                        steps = stepsNow,
                        weeklySteps = weeklyNow
                    )
                }
            )
            settingsScreen(
                onLogoutClick = { networkViewModel.logout() },
                userLogin = networkState.value.userLogin,
                userStepLength = settingsViewModel.settingsState.value.savedUserStep,
                userScale = settingsViewModel.settingsState.value.savedScale,
                onThemeClick = { settingsViewModel.changeTheme(it) },
                onNickNameClick = { login, newLogin ->
                    networkViewModel.changeLogin(
                        login,
                        newLogin
                    )
                },
                onStepLengthClick = { settingsViewModel.changeStepLength(it) },
                currentTheme = settingsViewModel.settingsState.value.savedTheme,
                onScaleClick = { settingsViewModel.changeScale(it) }
            )
        }
    }
}

