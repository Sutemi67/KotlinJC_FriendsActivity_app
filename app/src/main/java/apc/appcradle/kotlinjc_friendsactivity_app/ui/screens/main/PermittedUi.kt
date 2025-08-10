package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviews
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import kotlin.math.roundToInt

@Composable
fun PermittedUi(
    isStepSensorsAvailable: Boolean,
    stepCount: Int,
    userStepLength: Double,
    isServiceRunning: Boolean,
    onTrueCallback: () -> Unit,
    onFalseCallback: () -> Unit,
) {
    var km by remember { mutableDoubleStateOf(0.0) }
    var kkal by remember { mutableStateOf(IntRange(1, 2)) }

    LaunchedEffect(stepCount) {
        km = (stepCount * userStepLength / 1000 * 100.0).roundToInt() / 100.0
        kkal = kkalCalc(userStepLength, stepCount)
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
            Text(
                text = "Шагомер не поддерживается на этом устройстве",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
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
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = "$stepCount"
                    )
                    Text(
                        text = "Твой результат",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.width(90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text("$km")
                    Text("Километров")
                }
                Column(
                    modifier = Modifier.width(90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$kkal")
                    Text("Калорий")
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
                Text(
                    text = "Включить счетчик",
                    style = TextStyle(
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
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

private fun kkalCalc(userStepLength: Double, stepCount: Int): IntRange {
    val firstValue = (50 * (stepCount * userStepLength / 2500)).roundToInt()
    val secondValue = (75 * (stepCount * userStepLength / 2500)).roundToInt()
    return IntRange(firstValue, secondValue)
}

@ThemePreviews
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        Surface(
            tonalElevation = 1.dp
        ) {
            PermittedUi(
                true,
                stepCount = 454345,
                isServiceRunning = true,
                userStepLength = 0.4,
                onTrueCallback = {},
                onFalseCallback = {}
            )
        }
    }
}