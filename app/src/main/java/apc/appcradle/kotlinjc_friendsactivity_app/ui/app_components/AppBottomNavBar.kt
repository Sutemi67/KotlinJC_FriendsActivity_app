package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations

@Composable
fun AppBottomNavBar(
    modifier: Modifier = Modifier,
    navDestinations: List<Destinations>,
    currentRoute: String?,
    onNavigate: (Destinations) -> Unit
) {
    if (currentRoute == Destinations.AUTH.route &&
        currentRoute == Destinations.REGISTER.route
    ) return

    NavigationBar(
        modifier = modifier,
        containerColor = Color.Transparent.copy(alpha = 0.05f)
    ) {

        navDestinations.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.iconSelected else item.iconUnselected,
                        contentDescription = stringResource(item.label),
                    )
                },
                label = {
                    AppComponents.AppText(
                        stringResource(item.label),
                        appTextStyle = AppTextStyles.Body
                    )
                },
            )
        }
    }
}