package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviewsNoUi
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun StatisticRow(
    stepsTarget: Distances,
    overallDistanceKm: Double
) {
    val textStyle: TextStyle = if (overallDistanceKm in stepsTarget.range) {
        MaterialTheme.typography.bodyLarge
    } else if (overallDistanceKm >= stepsTarget.distance) {
        TextStyle(
            textDecoration = TextDecoration.LineThrough,
            color = Color.Gray.copy(alpha = 0.8f)
        )
    } else {
        TextStyle(color = Color.Gray.copy(alpha = 0.8f))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = stepsTarget.destination, style = textStyle)
        Text(text = "${stepsTarget.distance.toInt().toString()} км", style = textStyle)
    }
}

@ThemePreviewsNoUi
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        StatisticRow(Distances.Pivo, 10.50)
    }
}