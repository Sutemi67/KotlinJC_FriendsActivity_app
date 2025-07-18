package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import org.koin.compose.koinInject

@Composable
fun MainUserScreen(
    viewModel: MainViewModel,
) {
    val sensorManager = koinInject<AppSensorsManager>()
    val stepCount by sensorManager.stepsData.collectAsState()
    val state = viewModel.state.collectAsState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (!state.value.isPermissionsGet) {
                UnpermittedUi(onGetPermissionsClick = { viewModel.getPermission() })
            } else {
                PermittedUi(
                    viewModel = viewModel,
                    sensorManager = sensorManager,
                    stepCount = stepCount
                )
            }
        }
    }

}

