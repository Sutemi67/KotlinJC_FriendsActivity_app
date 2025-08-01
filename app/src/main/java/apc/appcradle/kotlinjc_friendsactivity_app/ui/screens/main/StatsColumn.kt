package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun StatsColumn(
    overallDistanceKm: Double
) {
    Column {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Осталось только...",
            fontSize = 18.sp
        )
        StatisticRow(Distances.Pivo, overallDistanceKm)
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

enum class Distances(
    val destination: String,
    val distance: Double,
    val range: ClosedFloatingPointRange<Double>,
) {
    Pivo(destination = "сходить за пивом", distance = 1.0, range = 0.0..1.0),
    Marafon(destination = "прошагать Марафонскую дистанцию", 42.0, range = 1.0..42.0),
    Gran(destination = "пройти Гран Каньон", 446.0, range = 42.0..446.0),
    Likiy(destination = "пройти по Ликийской тропе", 540.0, 446.0..540.0),
    Gobi(destination = "сквозь пустыню Гоби", 800.0, 540.0..800.0),
    EarthCenter(destination = "дойти до центра Земли", 6371.0, 800.0..6371.0),
    Russia(destination = "пересечь Россию", 10000.0, 6371.0..10000.0),
    Ocean(destination = "по дну через Тихий океан", 19500.0, 10000.0..19500.0),
    AcrossEarth(destination = "обогнуть вокруг Земли", distance = 40075.0, 19500.0..40075.0),
    ToTheMoon(destination = "пешком до Луны", 384400.0, 40075.0..384400.0),
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        StatsColumn(50.0)
    }
}