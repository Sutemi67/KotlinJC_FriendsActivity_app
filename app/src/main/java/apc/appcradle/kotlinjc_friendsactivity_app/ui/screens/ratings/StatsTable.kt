package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.PreviewsDifferentSizes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun StatsTable(distance: Double) {
    val headerSize = typography.headlineLarge
    val textSize = typography.bodyLarge

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(
//            modifier = Modifier.padding(bottom = 10.dp),
//            text = "Статистика игроков",
//            style = headerSize
//        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Чемпион прошлой недели: ", style = textSize)
            Text("конечно же ты <3", style = textSize)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("В общем пройдено: ", style = textSize)
            Text("${distance.toInt()} км.", style = textSize)
        }
    }
}

@PreviewsDifferentSizes
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        StatsTable(4334.3)
    }
}