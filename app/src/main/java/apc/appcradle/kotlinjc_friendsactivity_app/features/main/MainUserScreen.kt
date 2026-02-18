package apc.appcradle.kotlinjc_friendsactivity_app.features.main

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.StepCounterService
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
    mainViewModel: MainViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val mainState = mainViewModel.state.collectAsState()
    val settingsState = settingsViewModel.state.collectAsState()

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
    mainState: State<MainScreenState>,
    settingsState: State<SettingsState>,
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
    val isRunningState = StepCounterService.isRunning.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val savedServiceOption = settingsState.value.serviceSavedOption
        val isRunning = isRunningState.value

        if (!mainState.value.isPermissionsGet) {
            UnpermittedUi(
                onGetPermissionsClick = {
                    permissionLauncher.launch(permissionManager.requiredPermissions.toTypedArray())
                }
            )
        } else {
            LaunchedEffect(savedServiceOption, isRunning) {
                if (savedServiceOption && !isRunning) startService(context)
            }
        }
        PermittedUi(
            serviceState = settingsState,
            isStepSensorsAvailable = mainState.value.isSensorsAvailable,
            summarySteps = mainState.value.userAllSteps,
            weeklySteps = mainState.value.userWeeklySteps,
            counterCheckerCallback = { serviceCheckerCallback(it, context) },
            userStepLength = settingsState.value.userStepLength,
            isLoading = mainState.value.isLoading
        )
    }
}
