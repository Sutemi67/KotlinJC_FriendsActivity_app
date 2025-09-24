package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.Distances
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun StatsColumn(
    overallDistanceKm: Double
) {
    Column {
        AppText(
            modifier = Modifier.padding(10.dp),
            text = "Осталось только...",
            appTextStyle = AppTextStyles.Header
        )
        StatisticRow(Distances.Beer, overallDistanceKm)
        StatisticRow(Distances.Marafon, overallDistanceKm)
        StatisticRow(Distances.Gran, overallDistanceKm)
        StatisticRow(Distances.Likiy, overallDistanceKm)
        StatisticRow(Distances.Gobi, overallDistanceKm)
        StatisticRow(Distances.EarthCenter, overallDistanceKm)
        StatisticRow(Distances.Russia, overallDistanceKm)
        StatisticRow(Distances.Ocean, overallDistanceKm)
        StatisticRow(Distances.AcrossEarth, overallDistanceKm)
        StatisticRow(Distances.ToTheMoon, overallDistanceKm)
    }
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        StatsColumn(50.0)
    }
}