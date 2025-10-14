package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import apc.appcradle.kotlinjc_friendsactivity_app.NetworkViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.LocalSensorManager
import org.koin.compose.koinInject

@Composable
fun MainUserScreen(
    viewModel: NetworkViewModel
) {
    val sensorsManager = LocalSensorManager.current
    val permissionManager = koinInject<PermissionManager>()
    val state by viewModel.networkState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        permissionManager.onPermissionResult(allGranted)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!state.isPermissionsGet) {
            UnpermittedUi(
                onGetPermissionsClick = {
                    // Сначала запрашиваем runtime-разрешения
                    permissionLauncher.launch(permissionManager.requiredPermissions.toTypedArray())
                    // Затем, если точные будильники не разрешены, открываем системные настройки
                    if (!permissionManager.isExactAlarmAllowed()) {
                        permissionManager.openExactAlarmSettings()
                    }
                }
            )
        } else {
            // Автозапуск, если флаг включен и сервис не запущен (однократно)
            LaunchedEffect(state.isServiceEnabled) {
                if (state.isServiceEnabled && !state.isServiceRunning) {
                    viewModel.startService(context)
                }
            }
            PermittedUi(
                isStepSensorsAvailable = sensorsManager.isStepSensorAvailable,
                summarySteps = sensorsManager.allSteps.collectAsState().value,
                weeklySteps = sensorsManager.weeklySteps.collectAsState().value,
                isServiceRunning = state.isServiceRunning || state.isServiceEnabled,
                onTrueCallback = { viewModel.startService(context) },
                onFalseCallback = { viewModel.stopService(context) },
                userStepLength = state.userStepLength
            )
        }
    }
}

