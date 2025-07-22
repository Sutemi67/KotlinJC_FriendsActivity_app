package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
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
    private val networkClient: NetworkClient
) : ViewModel() {

    private var _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var _isSend = MutableStateFlow<Boolean?>(null)
    val isSend: StateFlow<Boolean?> = _isSend.asStateFlow()

    init {
        viewModelScope.launch {
            permissionManager.permissionsGranted.collect { isGranted ->
                _state.update { it.copy(isPermissionsGet = isGranted) }
            }
        }
    }

    fun isRunning(context: Context) {
        val isRun: Boolean = isServiceRunning(context, StepCounterService::class.java)
        _state.update { it.copy(isServiceRunning = isRun) }
    }

    fun startService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.startService(serviceIntent)
        isRunning(context)
    }

    fun stopService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.stopService(serviceIntent)
        isRunning(context)

    }

    suspend fun sendRegisterData(login: String, password: String): Boolean? {
//        viewModelScope.launch {
            val isSend = networkClient.sendRegistrationInfo(login, password)
            if (isSend) {
                Log.i("dataTransfer", "viewModel transfer - OK")
                _isSend.update { true }
            } else {
                Log.e("dataTransfer", "viewModel transfer not successful")
                _isSend.update { false }
            }
//        }
        return isSend
    }
}
