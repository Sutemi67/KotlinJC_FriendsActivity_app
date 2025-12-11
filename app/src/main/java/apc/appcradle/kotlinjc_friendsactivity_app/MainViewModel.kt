package apc.appcradle.kotlinjc_friendsactivity_app

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.data.SERVICE_RESTART_TAG
import apc.appcradle.kotlinjc_friendsactivity_app.data.createServiceRestartRequest
import apc.appcradle.kotlinjc_friendsactivity_app.data.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.data.TokenRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.WORKER_TAG
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.SharedPreferencesData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.domain.StepCounterService
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
    private val tokenRepositoryImpl: TokenRepositoryImpl,
    private val settingsPreferencesImpl: SettingsRepository,
    private val statsRepository: StatsRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private var _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var _transferState = MutableStateFlow(DataTransferState())
    val transferState: StateFlow<DataTransferState> = _transferState.asStateFlow()

    init {
        checkPermanentAuth()

        viewModelScope.launch {
            workManager.pruneWork()
            workManager.getWorkInfosByTagFlow(WORKER_TAG).collect {
                it.forEach { element ->
                    Log.i(
                        "worker",
                        "statRepo,workerInfoAsync -> ${element.id}, ${element.state}, ${element.initialDelayMillis}"
                    )
                }
            }
        }

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
            _state.update { it.copy(isServiceEnabled = true) }
            saveSettings()
        } catch (e: Exception) {
            Log.e("service", "Failed to start service: ${e.message}")
        }
    }

    fun stopService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.stopService(serviceIntent)
        isServiceRunning(context)
        _state.update { it.copy(isServiceEnabled = false) }
        saveSettings()
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }

    fun triggerServiceRestartCheck() {
        viewModelScope.launch {
            try {
                // Cancel any existing restart requests to avoid duplicates
                workManager.cancelAllWorkByTag(SERVICE_RESTART_TAG)
                
                // Schedule a new restart check
                val restartRequest = createServiceRestartRequest(delayMillis = 5_000L) // 5 seconds delay
                workManager.enqueue(restartRequest)
                
                Log.i("service", "Manual service restart check triggered")
            } catch (e: Exception) {
                Log.e("service", "Failed to trigger service restart check: ${e.message}")
            }
        }
    }

    //endregion

    //region Authentification
    fun logout() {
        tokenRepositoryImpl.clearToken()
        _state.update {
            it.copy(
                isLoggedIn = false,
                userLogin = null
            )
        }
        _transferState.update { it.copy(isSuccessful = null, errorMessage = null) }
    }

    fun goOfflineUse() {
        tokenRepositoryImpl.saveOfflineToken()
        _state.update {
            it.copy(
                isLoggedIn = true,
                userLogin = null
            )
        }
    }

    private fun checkPermanentAuth() {
        val token = tokenRepositoryImpl.getToken()
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
                val login = tokenRepositoryImpl.getLogin()
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
                tokenRepositoryImpl.saveNewLogin(newLogin)
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
            SharedPreferencesData(
                savedTheme = state.value.currentTheme,
                savedScale = state.value.userScale,
                savedUserStep = state.value.userStepLength,
                savedIsServiceEnabled = state.value.isServiceEnabled
            )
        )
    }

    private fun loadSettings() {
        val settings = settingsPreferencesImpl.loadSettingsData()
        _state.update {
            it.copy(
                currentTheme = settings.savedTheme,
                userStepLength = settings.savedUserStep,
                userScale = settings.savedScale,
                isServiceEnabled = settings.savedIsServiceEnabled
            )
        }
    }
    //endregion

    //region Sync Data

    suspend fun syncData(login: String, steps: Int, weeklySteps: Int): PlayersListSyncData {
        return withContext(Dispatchers.IO) {
            val result = statsRepository.syncData(
                login = login, steps = steps, weeklySteps = weeklySteps
            )
            Log.i("dataTransfer", "ViewModel sync result: $result")
            result
        }
    }
//endregion
}
