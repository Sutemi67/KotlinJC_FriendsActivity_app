package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.Distances
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.utils.APP_ROUNDED_SHAPE
import apc.appcradle.kotlinjc_friendsactivity_app.utils.USER_STEP_DEFAULT
import apc.appcradle.kotlinjc_friendsactivity_app.utils.format

@Composable
fun PlayerStatsView(
    modifier: Modifier = Modifier,
    login: String?,
    playerActivityData: PlayerActivityData,
) {
    var percentage by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = playerActivityData.percentage
        ) { value, _ ->
            percentage = value
        }
    }

    val percentageAnimate by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 3000)
    )

    val userBgColor =
        if (login == playerActivityData.login) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }

    ElevatedCard(
        modifier = modifier
            .height(60.dp)
            .padding(vertical = 1.dp),
        shape = APP_ROUNDED_SHAPE
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRoundRect(
                        cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                        color = userBgColor,
                        size = Size(width = size.width * percentageAnimate, height = size.height),
                    )
                }
        ) {
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
                        text = stringResource(getDistanceByValue(playerActivityData.steps * USER_STEP_DEFAULT / 1000).destination),
                        appTextStyle = AppTextStyles.Label
                    )
                }
                AppText(
                    color = MaterialTheme.colorScheme.onSurface,
                    text = format(playerActivityData.weeklySteps)
                )
            }
        }
    }
}

private fun getDistanceByValue(value: Double): Distances =
    Distances.entries.lastOrNull { value in it.range } ?: Distances.entries.last()

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        PlayerStatsView(
            login = "Alexander",
            playerActivityData = PlayerActivityData(
                "Alexander",
                3423,
                weeklySteps = 27772,
                0.4f //для корректного отображения поменять исходный percentage в функции
            ),
        )
    }
}
