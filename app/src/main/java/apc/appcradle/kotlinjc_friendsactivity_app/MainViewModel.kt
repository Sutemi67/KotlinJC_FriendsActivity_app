package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.TokenStorage
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
    private val networkClient: NetworkClient,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private var _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var _isSend = MutableStateFlow<Boolean?>(null)
    val isSend: StateFlow<Boolean?> = _isSend.asStateFlow()

    init {
        checkAuth()
        viewModelScope.launch {
            permissionManager.permissionsGranted.collect { isGranted ->
                _state.update { it.copy(isPermissionsGet = isGranted) }
            }
        }
    }

    private fun checkAuth() {
        val token = tokenStorage.getToken()
        if (token != null) {
            _state.update { it.copy(isLoggedIn = true) }
        }
        Log.d("dataTransfer", "Token is valid. Loading main screen...")
    }

    fun logout() {
        tokenStorage.clearToken()
        _state.update { it.copy(isLoggedIn = false) }
        _isSend.update { null } // Reset registration state
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

    fun sendRegisterData(login: String, password: String) {
        viewModelScope.launch {
            val isSuccess = networkClient.sendRegistrationInfo(login, password)
            if (isSuccess) {
                Log.i("dataTransfer", "viewModel transfer - OK")
                _isSend.update { true }
                _state.update { it.copy(isLoggedIn = true) }
            } else {
                Log.e("dataTransfer", "viewModel transfer not successful")
                _isSend.update { false }
            }
        }
    }
}
