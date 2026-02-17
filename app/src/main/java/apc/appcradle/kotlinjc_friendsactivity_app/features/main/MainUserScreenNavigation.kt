package apc.appcradle.kotlinjc_friendsactivity_app.features.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations
import org.koin.androidx.compose.koinViewModel

fun NavController.toMainScreen() {
    navigate(route = Destinations.MAIN.route) {
        // Очищаем всё ДО главного экрана, но сам MAIN не удаляем
        popUpTo(Destinations.MAIN.route) {
            inclusive = false // Важно: false
        }
        launchSingleTop = true // Не создаем новую копию, если MAIN уже наверху
    }
}

fun NavGraphBuilder.mainScreen() {
    composable(Destinations.MAIN.route) {
        val mainViewModel = koinViewModel<MainViewModel>()
        MainUserScreen(mainViewModel)
    }
}