package apc.appcradle.kotlinjc_friendsactivity_app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import org.koin.compose.koinInject

@Composable
fun MainAppComposable(
    modifier: Modifier = Modifier,
    isPermissionsGranted: Boolean = true,
    onStopClick: () -> Unit
) {
    val sensorManager = koinInject<AppSensorsManager>()
    val stepCount by sensorManager.stepsData.collectAsState()

    Box(modifier.fillMaxSize()) {
        if (!isPermissionsGranted) {
            Text(
                modifier = Modifier.padding(15.dp),
                text = "Для работы шагомера требуется разрешение на распознавание активности."
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Шагов пройдено: $stepCount",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        sensorManager.stopCounting()
                        onStopClick()
                    }
                ) {
                    Text("Выйти из гонки")
                }
            }
        }
    }
}