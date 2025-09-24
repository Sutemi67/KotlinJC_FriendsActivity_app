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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun StatsTable(
    distance: Double,
    leaderDifference: Double,
    leader: String?
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
            AppText(
                text = stringResource(R.string.ratings_screen_champion),
                modifier = Modifier.weight(1f)
            )
            AppText(
                text = leader ?: stringResource(R.string.ratings_screen_of_course_you),
                singleLine = true
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(stringResource(R.string.ratings_screen_all_distance))
            AppText(stringResource(R.string.main_screen_destination_progress, distance.toInt()))
        }
        HorizontalDivider(Modifier.padding(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = stringResource(R.string.ratings_screen_leader_diff),
                modifier = Modifier.weight(1f)
            )
            AppText(
                text = if (leaderDifference != 0.0)
                    stringResource(
                        R.string.main_screen_destination_progress,
                        leaderDifference.toInt()
                    )
                else "-",
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        StatsTable(4334.3, leaderDifference = 3.0, leader = null)
    }
}