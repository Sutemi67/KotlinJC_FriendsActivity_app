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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val permissionManager: PermissionManager,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            permissionManager.permissionsGranted.collect { isGranted ->
                _state.update { it.copy(isPermissionsGet = isGranted) }
            }
        }
        // Проверяем разрешения при инициализации
        _state.update { it.copy(isPermissionsGet = permissionManager.arePermissionsGranted()) }
    }

    fun startService() {
        if (permissionManager.arePermissionsGranted()) {
            val serviceIntent = Intent(context, StepCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(context, serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } else {
            getPermission()
        }
    }

    fun stopService() {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.stopService(serviceIntent)
    }

    fun getPermission() {
        if (context is ComponentActivity) {
            permissionManager.registerPermissionHandler(context) { isGranted ->
                if (isGranted) {
                    startService()
                }
            }
        }
    }

    fun processAction(appAction: AppActions) {
        when (appAction) {
            is AppActions.ChangeState -> {
                _state.update { it.copy(isPermissionsGet = appAction.state) }
            }
        }
    }
}
