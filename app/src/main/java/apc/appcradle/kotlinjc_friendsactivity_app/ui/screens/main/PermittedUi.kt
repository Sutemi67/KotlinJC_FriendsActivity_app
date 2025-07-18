package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviews
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PermittedUi(
    viewModel: MainViewModel,
    stepCount: Int,
    sensorManager: AppSensorsManager,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Шагов пройдено: $stepCount",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                viewModel.startService(context)
            }
        ) {
            Text("Начать забег")
        }
        Button(
            onClick = {
                sensorManager.stopCounting()
                viewModel.stopService(context)
            }
        ) {
            Text("Выйти из гонки")
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        PermittedUi(
            stepCount = 34,
            sensorManager = koinInject<AppSensorsManager>(),
            viewModel = koinViewModel()
        )
    }
}