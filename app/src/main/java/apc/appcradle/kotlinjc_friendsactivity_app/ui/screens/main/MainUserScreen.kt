package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.permissions.PermissionManager
import org.koin.compose.koinInject

@Composable
fun MainUserScreen(
    viewModel: MainViewModel,
) {
    val permissionManager = koinInject<PermissionManager>()

    val state by viewModel.state.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        permissionManager.onPermissionResult(allGranted)
    }
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (!state.isPermissionsGet) {
                UnpermittedUi(
                    onGetPermissionsClick = {
                        permissionLauncher.launch(permissionManager.requiredPermissions.toTypedArray())
                    }
                )
            } else {
                PermittedUi(viewModel = viewModel)
            }
        }
    }

}

