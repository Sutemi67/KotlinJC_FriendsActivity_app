package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.login.nav.loginScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.registerScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration.nav.toRegisterScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.mainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings.nav.ratingsScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings.nav.settingsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationHost(
) {
    val viewModel: MainViewModel = koinViewModel()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val noLoginDestinations = Destinations.entries.filter { it != Destinations.LOGIN }

    Scaffold(
        bottomBar = {
            if (navBackStackEntry?.destination?.route != Destinations.LOGIN.route &&
                navBackStackEntry?.destination?.route != Destinations.REGISTER.route
            )
                NavigationBar {
                    noLoginDestinations.forEachIndexed { index, item ->
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
        NavHost(
            modifier = Modifier.padding(contentPadding),
            navController = navController,
            startDestination = Destinations.LOGIN.route
        ) {
            loginScreen(
                toMainScreen = { navController.toMainScreen() },
                onRegisterClick = { navController.toRegisterScreen() }
            )
            registerScreen(viewModel, navController)
            mainScreen(viewModel)
            ratingsScreen(viewModel)
            settingsScreen()
        }
    }
}