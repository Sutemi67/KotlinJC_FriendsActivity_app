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
    val settingsState = settingsViewModel.settingsState.collectAsState()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val noAuthDestinations =
        if (networkState.value.userLogin == null) {
            Destinations.entries.filter { it != Destinations.AUTH && it != Destinations.REGISTER && it != Destinations.RATINGS }
        } else {
            Destinations.entries.filter { it != Destinations.AUTH && it != Destinations.REGISTER }
        }

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
                toRegisterScreen = navController::toRegisterScreen,
                networkState = networkState,
                sendLoginData = networkViewModel::sendLoginData,
                onOfflineUseClick = networkViewModel::goOfflineUse
            )
            registerScreen(
                toMainScreen = navController::toMainScreen,
                sendRegisterCallback = networkViewModel::sendRegisterData,
                networkState = networkState
            )
            mainScreen(
                settingsViewModel = settingsViewModel,
                serviceViewModel = serviceViewModel
            )
            ratingsScreen(
                networkState = networkState,
                syncFun = {
                    networkViewModel.syncData(
                        login = networkState.value.userLogin!!,
                        steps = serviceViewModel.allSteps.value,
                        weeklySteps = serviceViewModel.weeklySteps.value
                    )
                }
            )
            settingsScreen(
                settingsState = settingsState,
                networkState = networkState,
                onLogoutClick = networkViewModel::logout,
                onThemeClick = settingsViewModel::changeTheme,
                onNickNameClick = networkViewModel::changeLogin,
                onStepLengthClick = settingsViewModel::changeStepLength,
                onScaleClick = settingsViewModel::changeScale
            )
        }
    }
}

