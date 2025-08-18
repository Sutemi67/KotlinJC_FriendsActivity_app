package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.PreviewsDifferentSizes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun StatsTable(
    distance: Double,
    leaderDifference: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppText(text = "Чемпион прошлой недели:", modifier = Modifier.weight(1f))
            AppText(text = "конечно же ты <3", singleLine = true)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppText("В общем пройдено:")
            AppText("${distance.toInt()} км.")
        }
        HorizontalDivider(Modifier.padding(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = "Текущее отставание от лидера: ",
                modifier = Modifier.weight(1f)
            )
            AppText(
                text = if (leaderDifference != 0.0)
                    "${leaderDifference.toInt()} км."
                else "-",
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@PreviewsDifferentSizes
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        StatsTable(4334.3, leaderDifference = 0.0)
    }
}