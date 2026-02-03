package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.LocalSensorManager
import org.koin.compose.koinInject

@Composable
fun MainUserScreen(
    viewModel: MainViewModel
) {
    val sensorsManager = LocalSensorManager.current
    val permissionManager = koinInject<PermissionManager>()
    val state by viewModel.state.collectAsState()
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
            if (state.isServiceEnabledByUser && !state.isServiceRunning)
                viewModel.startService(context)
        }
        PermittedUi(
            isStepSensorsAvailable = sensorsManager.isStepSensorAvailable,
            summarySteps = sensorsManager.allSteps.collectAsState().value,
            weeklySteps = sensorsManager.weeklySteps.collectAsState().value,
            isServiceRunning = state.isServiceRunning,
            counterCheckerCallback = { viewModel.userServiceCheckerListener(it, context) },
            userStepLength = state.userStepLength,
            isLoading = state.isLoading
        )
    }
}

