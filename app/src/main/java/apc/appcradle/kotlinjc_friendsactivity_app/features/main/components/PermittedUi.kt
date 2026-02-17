package apc.appcradle.kotlinjc_friendsactivity_app.features.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.format
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun PermittedUi(
    isStepSensorsAvailable: Boolean,
    summarySteps: Int,
    weeklySteps: Int = 0,
    userStepLength: Double,
    isServiceRunning: Boolean,
    isLoading: Boolean,
    counterCheckerCallback: (Boolean) -> Unit,
) {
    var kmWeekly by remember { mutableDoubleStateOf(0.0) }
    var kmAll by remember { mutableDoubleStateOf(0.0) }
    var kkal by remember { mutableStateOf(IntRange(1, 2)) }

    LaunchedEffect(summarySteps) {
        kmWeekly = (weeklySteps * userStepLength / 1000 * 100.0).roundToInt() / 100.0
        kmAll = (summarySteps * userStepLength / 1000 * 100.0).roundToInt() / 100.0
        kkal = kkalCalc(userStepLength, weeklySteps)
    }

    if (!isStepSensorsAvailable) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppText(
                text = stringResource(R.string.main_screen_not_support),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedCard {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .padding(1.dp)
                            .height(40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(25.dp)
                            )
                        } else {
                            AppText(
                                text = format(weeklySteps),
                                appTextStyle = AppTextStyles.MainCounter
                            )
                        }
                        Spacer(Modifier.width(20.dp))
                        Icon(
                            modifier = Modifier.fillMaxHeight(),
                            painter = painterResource(R.drawable.outline_steps_24),
                            contentDescription = null
                        )
                    }
                    AppText(
                        text = stringResource(R.string.main_screen_your_result),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppText(text = format(kmWeekly))
                    AppText(text = stringResource(R.string.main_screen_km))
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AppText(
                        text = stringResource(
                            R.string.main_screen_kkal_range,
                            format(kkal.first),
                            format(kkal.last)
                        )
                    )
                    AppText(text = stringResource(R.string.main_screen_kkal))
                }
            }
            HorizontalDivider(
                Modifier
                    .padding(5.dp)
                    .padding(horizontal = 30.dp)
            )
            StatsColumn(kmAll)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppText(
                text = stringResource(R.string.main_screen_sensor_on),
                appTextStyle = AppTextStyles.Header
            )
            Switch(
                checked = isServiceRunning,
                onCheckedChange = { counterCheckerCallback(it) }
            )
        }
    }
}

private fun kkalCalc(userStepLength: Double, stepCount: Int): IntRange {
    val firstValue = (50 * (stepCount * userStepLength / 2500)).roundToInt()
    val secondValue = (75 * (stepCount * userStepLength / 2500)).roundToInt()
    return IntRange(firstValue, secondValue)
}

@Preview
@Composable
private fun PreviewUi() {
    KotlinJC_FriendsActivity_appTheme {
        PermittedUi(
            true,
            summarySteps = 454345,
            weeklySteps = 4433,
            isServiceRunning = true,
            userStepLength = 33.2,
            isLoading = true,
            counterCheckerCallback = {}
        )
    }
}