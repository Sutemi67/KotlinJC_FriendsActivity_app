package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.Distances
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun PlayerStatsView(
    modifier: Modifier = Modifier,
    login: String?,
    playerActivityData: PlayerActivityData,
) {
    val percentageAnimate = animateFloatAsState(
        targetValue = playerActivityData.percentage,
        animationSpec = tween(durationMillis = 3000)
    )
    LaunchedEffect(playerActivityData) {
        Log.i("debug", "$login, ${playerActivityData.login}, ${playerActivityData.percentage}")
    }
    ElevatedCard(
        modifier = modifier
            .height(60.dp)
            .padding(vertical = 4.dp, horizontal = 15.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (login == playerActivityData.login) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .fillMaxWidth(percentageAnimate.value)
                    .fillMaxHeight()
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    AppText(
                        color = MaterialTheme.colorScheme.onSurface,
                        text = playerActivityData.login
                    )
                    AppText(
                        color = MaterialTheme.colorScheme.onSurface,
                        text = getDistanceByValue(playerActivityData.steps * 0.4 / 1000).destination,
                        appTextStyle = AppTextStyles.Label
                    )
                }
                AppText(
                    color = MaterialTheme.colorScheme.onSurface,
                    text = "${playerActivityData.steps}"
                )
            }
        }
    }
}

private fun getDistanceByValue(value: Double): Distances {
    return Distances.entries.toTypedArray().lastOrNull { value in it.range }
        ?: Distances.entries.last()
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        PlayerStatsView(
            login = "Alexander",
            playerActivityData = PlayerActivityData(
                "Alexander",
                3423,
                weeklySteps = 22,
                .3f
            ),
        )
    }
}
