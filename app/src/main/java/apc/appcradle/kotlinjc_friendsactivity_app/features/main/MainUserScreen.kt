package apc.appcradle.kotlinjc_friendsactivity_app.features.main

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.components.PermittedUi
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.components.UnpermittedUi
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.model.MainScreenState
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.SettingsViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsState
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainUserScreen(
    mainViewModel: MainViewModel ,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val mainState = mainViewModel.state.collectAsState().value
    val settingsState = settingsViewModel.state.collectAsState().value

    MainUserScreenUi(
        mainState = mainState,
        settingsState = settingsState,
        startService = { settingsViewModel.obtainEvent(SettingsEvents.StartService(it)) },
        serviceCheckerCallback = { boolean, context ->
            settingsViewModel.obtainEvent(
                SettingsEvents.OnServiceCheckerClick(
                    boolean,
                    context
                )
            )
        }
    )
}

@Composable
fun MainUserScreenUi(
    mainState: MainScreenState,
    settingsState: SettingsState,
    startService: (Context) -> Unit,
    serviceCheckerCallback: (Boolean, Context) -> Unit
) {
    val permissionManager = koinInject<PermissionManager>()
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
        if (!mainState.isPermissionsGet) {
            UnpermittedUi(
                onGetPermissionsClick = {
                    permissionLauncher.launch(permissionManager.requiredPermissions.toTypedArray())
                }
            )
        } else {
            LaunchedEffect(settingsState.serviceSavedOption, settingsState.isServiceRunning) {
                if (settingsState.serviceSavedOption && !settingsState.isServiceRunning) {
                    startService(context)
                }
            }
        }
        PermittedUi(
            isStepSensorsAvailable = mainState.isSensorsAvailable,
            summarySteps = mainState.userAllSteps,
            weeklySteps = mainState.userWeeklySteps,
            isServiceRunning = settingsState.isServiceRunning,
            counterCheckerCallback = { serviceCheckerCallback(it, context) },
            userStepLength = settingsState.userStepLength,
            isLoading = mainState.isLoading
        )
    }
}
