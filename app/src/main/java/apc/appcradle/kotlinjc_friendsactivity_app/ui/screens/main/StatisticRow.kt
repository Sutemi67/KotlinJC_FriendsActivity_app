package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.LocalAppTypography
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.Distances
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun StatisticRow(
    stepsTarget: Distances,
    overallDistanceKm: Double
) {
    val textStyle: TextStyle = if (overallDistanceKm in stepsTarget.range) {
        LocalAppTypography.current.bodyText
    } else if (overallDistanceKm >= stepsTarget.distance) {
        LocalAppTypography.current.bodyText.copy(
            textDecoration = TextDecoration.LineThrough,
            color = Color.Gray.copy(alpha = 0.8f)
        )
    } else {
        LocalAppTypography.current.bodyText.copy(
            color = Color.Gray.copy(alpha = 0.8f)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stepsTarget.destination,
            style = textStyle
        )
        Text(
            text = "${stepsTarget.distance.toInt()} км",
            style = textStyle
        )
    }
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        StatisticRow(Distances.Pivo, 10.50)
    }
}