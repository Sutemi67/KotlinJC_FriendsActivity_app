package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.LocalSensorManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.LocalViewModel

@Composable
fun PermittedUi(
    isStepSensorsAvailable: Boolean
) {
    if (isStepSensorsAvailable) {
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
    PermittedColumn()
}

@Composable
private fun PermittedColumn() {

    val viewModel = LocalViewModel.current
    val sensorManager = LocalSensorManager.current
    val context = LocalContext.current
    val state = viewModel.state.collectAsState()
    val stepCount by sensorManager.stepsData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Шагов пройдено:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.headlineLarge,
            text = "$stepCount"
        )
        Column(
            modifier = Modifier.padding(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Включить обновление шагов",
                    style = TextStyle(
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                Switch(
                    checked = state.value.isServiceRunning,
                    onCheckedChange = { state ->
                        when (state) {
                            true -> {
                                viewModel.startService(context)
                            }

                            false -> {
                                viewModel.stopService(context)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PermittedUi(false)
}