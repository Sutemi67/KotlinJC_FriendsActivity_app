package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppActions
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.permissions.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.StepCounterService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val permissionManager: PermissionManager,
    private val context: Context,
) : ViewModel() {

    val state = permissionManager.permissionsGranted.map { isGranted ->
        AppState(isPermissionsGet = isGranted)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppState())


    fun startService() {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.startService(serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.stopService(serviceIntent)
    }

    fun onAction(action: AppActions) {
        when (action) {
            is AppActions.ChangeState -> {
                // This is now handled by the permission flow
            }
        }
    }
}
