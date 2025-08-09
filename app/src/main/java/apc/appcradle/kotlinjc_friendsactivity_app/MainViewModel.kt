package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.data.SettingsPreferencesImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.TokenStorage
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.DataTransferState
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
    private val tokenStorage: TokenStorage,
    private val settingsPreferencesImpl: SettingsPreferencesImpl
) : ViewModel() {

    private var _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var _transferState = MutableStateFlow(DataTransferState())
    val transferState: StateFlow<DataTransferState> = _transferState.asStateFlow()

    init {
        checkPermanentAuth()
        viewModelScope.launch {
            permissionManager.permissionsGranted.collect { isGranted ->
                _state.update { it.copy(isPermissionsGet = isGranted) }
            }
        }
    }

    //region Service
    fun isServiceRunning(context: Context) {
        val isRun: Boolean = isServiceRunning(context, StepCounterService::class.java)
        _state.update { it.copy(isServiceRunning = isRun) }
    }

    fun startService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        try {
            ContextCompat.startForegroundService(context, serviceIntent)
            isServiceRunning(context)
        } catch (e: Exception) {
            Log.e("service", "Failed to start service: ${e.message}")
        }
    }

    fun stopService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.stopService(serviceIntent)
        isServiceRunning(context)
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }

    //endregion

    //region Authentification
    fun logout() {
        tokenStorage.clearToken()
        _state.update {
            it.copy(
                isLoggedIn = false,
                userLogin = null
            )
        }
        _transferState.update { it.copy(isSuccessful = null, errorMessage = null) }
    }

    private fun checkPermanentAuth() {
        val token = tokenStorage.getToken()

        if (token != null) {
            val login = tokenStorage.getLogin()
            _state.update {
                it.copy(
                    isLoggedIn = true,
                    userLogin = login
                )
            }
            Log.d("dataTransfer", "Token is valid. Loading main screen for login: $login")
        } else {
            Log.d("dataTransfer", "Permanent token is not valid...")
        }
    }

    fun sendLoginData(login: String, password: String) {
        viewModelScope.launch {
            _transferState.update { it.copy(isLoading = true) }
            val result = networkClient.sendLoginInfo(login, password)
            if (result.isSuccessful == true && result.errorMessage == null) {
                Log.i("dataTransfer", "viewModel transfer - OK")
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = true,
                        errorMessage = result.errorMessage
                    )
                }
                _state.update {
                    it.copy(
                        isLoggedIn = true, userLogin = login
                    )
                }
            } else {
                Log.e("dataTransfer", "viewModel transfer not successful")
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = result.isSuccessful,
                        errorMessage = result.errorMessage
                    )
                }
            }
        }
    }

    fun sendRegisterData(login: String, password: String) {
        viewModelScope.launch {
            _transferState.update { it.copy(isLoading = true) }
            val result = networkClient.sendRegistrationInfo(login, password)
            if (result.isSuccessful == true && result.errorMessage == null) {
                Log.i("dataTransfer", "viewModel transfer - OK")
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = true,
                        errorMessage = result.errorMessage
                    )
                }
                _state.update {
                    it.copy(
                        isLoggedIn = true,
                        userLogin = login
                    )
                }
            } else {
                Log.e("dataTransfer", "viewModel transfer error: ${result.errorMessage}")
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = result.isSuccessful,
                        errorMessage = result.errorMessage
                    )
                }
            }
        }
    }
    //endregion

    //region Settings
    fun changeTheme(appThemes: AppThemes) {
        when (appThemes) {
            AppThemes.Dark -> _state.update { it.copy(currentTheme = AppThemes.Dark) }
            AppThemes.Light -> _state.update { it.copy(currentTheme = AppThemes.Light) }
            AppThemes.System -> _state.update { it.copy(currentTheme = AppThemes.System) }
        }
        Log.d("theme", "viewModel theme is: ${state.value.currentTheme}")

    }
    //endregion
}
