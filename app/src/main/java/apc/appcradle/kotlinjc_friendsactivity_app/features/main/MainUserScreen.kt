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
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.features.LocalSensorManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.components.PermittedUi
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.components.UnpermittedUi
import org.koin.compose.koinInject

@Composable
fun MainUserScreen(
    vm: MainViewModel
) {
    val state = vm.state.collectAsState().value
    logger(LoggerType.Info, "isLoading = ${state.isUserStepsLoading}")
    MainUserScreenUi(
        state = state,
        startService = { vm.startService(it) },
        serviceCheckerCallback = { boolean, context ->
            vm.userServiceCheckerListener(
                boolean,
                context
            )
        }
    )
}

@Composable
fun MainUserScreenUi(
    state: AppState,
    startService: (Context) -> Unit,
    serviceCheckerCallback: (Boolean, Context) -> Unit
) {
    val sensorsManager = LocalSensorManager.current
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
        if (!state.isPermissionsGet) {
            UnpermittedUi(
                onGetPermissionsClick = {
                    permissionLauncher.launch(permissionManager.requiredPermissions.toTypedArray())
                }
            )
        } else {
            LaunchedEffect(state.isServiceEnabledByUser, state.isServiceRunning) {
                if (state.isServiceEnabledByUser && !state.isServiceRunning) {
                    startService(context)
                }
            }
        }
        PermittedUi(
            isStepSensorsAvailable = sensorsManager.isStepSensorAvailable,
            summarySteps = sensorsManager.allSteps.collectAsState().value,
            weeklySteps = sensorsManager.weeklySteps.collectAsState().value,
            isServiceRunning = state.isServiceRunning,
            counterCheckerCallback = { serviceCheckerCallback(it, context) },
            userStepLength = state.userStepLength,
            isLoading = state.isUserStepsLoading
        )
    }
}
