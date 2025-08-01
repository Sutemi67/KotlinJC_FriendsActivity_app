package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.authScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.registerScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav.ratingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav.settingsScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

val LocalSensorManager =
    compositionLocalOf<AppSensorsManager> { error("No sensor manager provided") }
val LocalViewModel =
    compositionLocalOf<MainViewModel> { error("no view model provided") }

@Composable
fun NavigationHost() {
    val viewModel: MainViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val noAuthDestinations =
        Destinations.entries.filter { it != Destinations.AUTH && it != Destinations.REGISTER }

    val sensorManager: AppSensorsManager = koinInject<AppSensorsManager>()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.isServiceRunning(context)
        Log.d("sensors", sensorManager.isStepSensorAvailable.toString())
    }

    CompositionLocalProvider(
        LocalViewModel provides viewModel,
        LocalSensorManager provides sensorManager
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
                                        if (navBackStackEntry?.destination?.route == item.route) item.iconSelected else item.iconUnselected,
                                        contentDescription = item.label,
                                    )
                                },
                                label = { Text(item.label) },
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
                    viewModel = viewModel,
                    toRegisterScreen = { navController.toRegisterScreen() }
                )
                registerScreen(
                    viewModel = viewModel,
                    toMainScreen = { navController.toMainScreen() }
                )
                mainScreen(
                    viewModel = viewModel
                )
                ratingsScreen(
                    login = state.userLogin
                )
                settingsScreen(
                    onLogoutClick = { viewModel.logout() },
                    userLogin = state.userLogin,
                    onThemeClick = {},
                    onNickNameClick = {},
                    onStepDistanceClick = {}
                )
            }
        }
    }
}
