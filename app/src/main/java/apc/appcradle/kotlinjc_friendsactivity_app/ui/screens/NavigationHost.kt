package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

@Composable
fun NavigationHost(
) {
    val viewModel: MainViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val noAuthDestinations =
        Destinations.entries.filter { it != Destinations.AUTH && it != Destinations.REGISTER }

    val sensorManager: AppSensorsManager = koinInject<AppSensorsManager>()

//    LaunchedEffect(state.isLoggedIn) {
//        if (!state.isLoggedIn) {
//            Log.d("dataTransfer", "goes to auth in launched effect in host")
//            navController.navigate(Destinations.AUTH.route) {
//                popUpTo(0) { inclusive = true }
//            }
//        }
//    }

    Scaffold(
        topBar = {
            if (navBackStackEntry?.destination?.route != Destinations.AUTH.route &&
                navBackStackEntry?.destination?.route != Destinations.REGISTER.route
            )
                AppComponents.AppTopBar(state.userLogin)
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
            registerScreen(viewModel) { navController.toMainScreen() }
            mainScreen(viewModel, sensorManager)
            ratingsScreen(viewModel, state.userLogin, sensorManager = sensorManager)
            settingsScreen(viewModel)
        }
    }
}