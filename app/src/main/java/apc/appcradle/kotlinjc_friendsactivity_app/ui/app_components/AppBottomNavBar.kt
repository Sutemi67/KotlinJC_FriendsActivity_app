package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations

@Composable
fun AppBottomNavBar(
    modifier: Modifier = Modifier,
    navDestinations: List<Destinations>,
    navBackStackEntry: NavBackStackEntry?,
    navController: NavController
) {
    if (navBackStackEntry?.destination?.route != Destinations.AUTH.route &&
        navBackStackEntry?.destination?.route != Destinations.REGISTER.route
    ) {
        NavigationBar(
            modifier = modifier,
            containerColor = Color.Transparent.copy(alpha = 0.05f)
        ) {
            navDestinations.forEach { item ->
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
}