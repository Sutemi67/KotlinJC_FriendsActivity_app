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
import apc.appcradle.kotlinjc_friendsactivity_app.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.ServiceViewModel
import org.koin.compose.koinInject

@Composable
fun MainUserScreen(
    serviceViewModel: ServiceViewModel
) {
    val permissionManager = koinInject<PermissionManager>()

    val isPermissionsGranted by permissionManager.permissionsGranted.collectAsState()
    val sensorsState by serviceViewModel.stepsDataState.collectAsState()
    val isServiceRunning by serviceViewModel.isServiceWorkingState.collectAsState()

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
        if (!isPermissionsGranted) {
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
            LaunchedEffect(isServiceRunning) {
                if (!isServiceRunning) {
                    serviceViewModel.startService(context)
                }
            }
            PermittedUi(
                isStepSensorsAvailable = sensorsState.isSensorsAvailable,
                summarySteps = sensorsState.userAllSteps,
                weeklySteps = sensorsState.userWeeklySteps,
                isServiceRunning = isServiceRunning,
                onTrueCallback = { serviceViewModel.startService(context) },
                onFalseCallback = { serviceViewModel.stopService(context) },
                userStepLength = 0.65 //todo исправить
            )
        }
    }
}

