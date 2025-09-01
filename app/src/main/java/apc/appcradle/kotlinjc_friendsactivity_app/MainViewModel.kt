package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.data.SettingsStorageImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.StatsRepo
import apc.appcradle.kotlinjc_friendsactivity_app.data.TokenStorageImpl
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsStorage
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppSavedSettingsData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.permissions.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.StepCounterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val permissionManager: PermissionManager,
    private val networkClient: NetworkClient,
    private val tokenStorageImpl: TokenStorageImpl,
    private val settingsPreferencesImpl: SettingsStorage,
    private val statsRepository: StatsRepo
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
        loadSettings()
        Log.i("scale", "${state.value.userScale} loaded")
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
        tokenStorageImpl.clearToken()
        _state.update {
            it.copy(
                isLoggedIn = false,
                userLogin = null
            )
        }
        _transferState.update { it.copy(isSuccessful = null, errorMessage = null) }
    }

    fun goOfflineUse() {
        tokenStorageImpl.saveOfflineToken()
        _state.update {
            it.copy(
                isLoggedIn = true,
                userLogin = null
            )
        }
    }

    private fun checkPermanentAuth() {
        val token = tokenStorageImpl.getToken()
        when (token) {
            "offline" -> {
                _state.update {
                    it.copy(
                        isLoggedIn = true,
                        userLogin = null
                    )
                }
            }

            null -> {
                Log.d("dataTransfer", "Permanent token is not valid...")
            }

            else -> {
                val login = tokenStorageImpl.getLogin()
                _state.update {
                    it.copy(
                        isLoggedIn = true,
                        userLogin = login
                    )
                }
                Log.d("dataTransfer", "Token is valid. Loading main screen for login: $login")
            }
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
                        isLoggedIn = true,
                        userLogin = login
                    )
                }
            } else {
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = result.isSuccessful,
                        errorMessage = result.errorMessage
                    )
                }
                Log.e("dataTransfer", "viewModel transfer not successfu\n${result.errorMessage}")
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

    fun changeLogin(login: String, newLogin: String) {
        viewModelScope.launch {
            if (networkClient.changeUserLogin(login, newLogin)) {
                Log.i("dataTransfer", "смена ника - ${true}")
                _state.update { it.copy(userLogin = newLogin) }
                tokenStorageImpl.saveNewLogin(newLogin)
                return@launch
            }
            Log.e("dataTransfer", "смена ника - ${false}")
        }
    }
    //endregion

    //region Settings
    fun changeTheme(currentTheme: AppThemes) {
        _state.update { it.copy(currentTheme = currentTheme) }
        saveSettings()
        Log.d("theme", "viewModel theme is: ${state.value.currentTheme}")
    }

    fun changeScale(newValue: Float) {
        _state.update { it.copy(userScale = newValue) }
        saveSettings()
    }

    fun changeStepLength(newStepLength: Double) {
        _state.update { it.copy(userStepLength = newStepLength) }
        saveSettings()
    }

    private fun saveSettings() {
        settingsPreferencesImpl.saveSettingsData(
            AppSavedSettingsData(
                savedTheme = state.value.currentTheme,
                savedScale = state.value.userScale,
                savedUserStep = state.value.userStepLength
            )
        )
    }

    private fun loadSettings() {
        val settings = settingsPreferencesImpl.loadSettingsData()
        _state.update {
            it.copy(
                currentTheme = settings.savedTheme,
                userStepLength = settings.savedUserStep,
                userScale = settings.savedScale
            )
        }
    }
    //endregion

    //region Sync Data

    suspend fun syncData(login: String, steps: Int): PlayersListSyncData {
        return withContext(Dispatchers.IO) {
            val result = statsRepository.syncData(login, steps)
            Log.e("dataTransfer", "ViewModel sync result: $result")
            result
        }
    }
//endregion
}
