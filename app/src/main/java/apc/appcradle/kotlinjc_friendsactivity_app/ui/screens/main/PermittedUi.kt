package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviews
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import kotlin.math.roundToInt

@Composable
fun PermittedUi(
    isStepSensorsAvailable: Boolean,
    summarySteps: Int,
    dailySteps: Int = 0,
    userStepLength: Double,
    isServiceRunning: Boolean,
    onTrueCallback: () -> Unit,
    onFalseCallback: () -> Unit,
) {
    var km by remember { mutableDoubleStateOf(0.0) }
    var kkal by remember { mutableStateOf(IntRange(1, 2)) }

    LaunchedEffect(summarySteps) {
        km = (summarySteps * userStepLength / 1000 * 100.0).roundToInt() / 100.0
        kkal = kkalCalc(userStepLength, summarySteps)
        Log.d("mainScreen", "Launched effect on stepCount")
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
                text = "Шагомер не поддерживается на этом устройстве",
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
                    AppText(
                        text = format(summarySteps),
                        appTextStyle = AppTextStyles.MainCounter
                    )
                    AppText(
                        text = "Твой результат",
                    )
                    AppText(
                        text = format(summarySteps),
                        appTextStyle = AppTextStyles.MainCounter
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
                    modifier = Modifier.width(150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppText(text = format(km))
                    AppText(text = "Километров")
                }
                Column(
                    modifier = Modifier.width(150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AppText(text = "$kkal")
                    AppText(text = "Калорий")
                }
            }
            HorizontalDivider(
                Modifier
                    .padding(5.dp)
                    .padding(horizontal = 30.dp)
            )
            StatsColumn(km)
        }
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(
                    text = "Включить счетчик",
                    appTextStyle = AppTextStyles.Header
                )
                Switch(
                    checked = isServiceRunning,
                    onCheckedChange = { state ->
                        when (state) {
                            true -> {
                                onTrueCallback()
                            }

                            false -> {
                                onFalseCallback()
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun format(text: Int): String {
    val ddd = DecimalFormat("###,###.##")
    return ddd.format(text)
}

private fun format(text: Double): String {
    val ddd = DecimalFormat("###,###.##")
    return ddd.format(text)
}

private fun kkalCalc(userStepLength: Double, stepCount: Int): IntRange {
    val firstValue = (50 * (stepCount * userStepLength / 2500)).roundToInt()
    val secondValue = (75 * (stepCount * userStepLength / 2500)).roundToInt()
    return IntRange(firstValue, secondValue)
}

@ThemePreviews
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        PermittedUi(
            true,
            summarySteps = 454345,
            isServiceRunning = true,
            userStepLength = 30.4,
            onTrueCallback = {},
            onFalseCallback = {}
        )
    }
}