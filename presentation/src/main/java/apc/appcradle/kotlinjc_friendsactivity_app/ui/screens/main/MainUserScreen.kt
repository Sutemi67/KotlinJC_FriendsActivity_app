package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import apc.appcradle.kotlinjc_friendsactivity_app.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.ServiceViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.SettingsViewModel
import org.koin.compose.koinInject

@Composable
fun MainUserScreen(
    serviceViewModel: ServiceViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val permissionManager = koinInject<PermissionManager>()
    val isPermissionsGranted = permissionManager.permissionsGranted.collectAsState()

    val sensorsState = serviceViewModel.sensorStatus
    val allStepsState = serviceViewModel.allSteps.collectAsState()
    val weeklyStepsState = serviceViewModel.weeklySteps.collectAsState()
    val isServiceRunning = serviceViewModel.isServiceWorkingState.collectAsState()
    val settingsState = settingsViewModel.settingsState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        permissionManager.onPermissionResult(allGranted)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!isPermissionsGranted.value) {
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
            LaunchedEffect(isServiceRunning.value) {
                if (!isServiceRunning.value && settingsState.value.savedIsServiceEnabled) {
                    serviceViewModel.startService()
                }
            }
            PermittedUi(
                isStepSensorsAvailable = sensorsState,
                summarySteps = allStepsState.value,
                weeklySteps = weeklyStepsState.value,
                isServiceRunning = isServiceRunning.value,
                onTrueCallback = {
                    serviceViewModel.startService()
                    settingsViewModel.toggleService()
                },
                onFalseCallback = {
                    serviceViewModel.stopService()
                    settingsViewModel.toggleService()
                },
                userStepLength = 0.65 //todo исправить
            )
        }
    }
}

