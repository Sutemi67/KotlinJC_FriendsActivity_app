package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.TokenRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.model.SharedPreferencesData
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.model.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AuthData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.SettingsData
import apc.appcradle.kotlinjc_friendsactivity_app.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.services.StepCounterService
import apc.appcradle.kotlinjc_friendsactivity_app.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.utils.TRANCATE_WORKER_TAG
import apc.appcradle.kotlinjc_friendsactivity_app.utils.formatDeadline
import apc.appcradle.kotlinjc_friendsactivity_app.utils.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val permissionManager: PermissionManager,
    private val networkClient: NetworkClient,
    private val tokenRepositoryImpl: TokenRepositoryImpl,
    private val settingsPreferencesImpl: SettingsRepository,
    private val statsRepository: StatsRepository,
    private val sensorsManager: AppSensorsManager,
    workManager: WorkManager
) : ViewModel() {
    private var _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    // 1. Поток чисто для темы и настроек (нужен в MainActivity)
    val settingsState: StateFlow<SettingsData> = _state
        .map {
            // Превращаем AppState в SettingsData
            SettingsData(it.currentTheme, it.userScale, it.userStepLength)
        }
        .distinctUntilChanged() // <--- ГЛАВНЫЙ ИГРОК: пропускает только если данные РЕАЛЬНО изменились
        .stateIn(
            scope = viewModelScope, // Жизненный цикл
            started = SharingStarted.WhileSubscribed(5000), // Оптимизация ресурсов
            initialValue = SettingsData()
        )


    // 2. Поток чисто для логина/навигации (нужен в NavigationHost)
    val authState: StateFlow<AuthData> = _state
        .map { AuthData(it.isLoggedIn, it.userLogin) }
        .distinctUntilChanged().stateIn(
            scope = viewModelScope, // Жизненный цикл
            started = SharingStarted.WhileSubscribed(5000), // Оптимизация ресурсов
            initialValue = AuthData()
        )

    private var _transferState = MutableStateFlow(DataTransferState())
    val transferState: StateFlow<DataTransferState> = _transferState.asStateFlow()

    private val work: List<WorkInfo>? =
        workManager.getWorkInfosForUniqueWork(TRANCATE_WORKER_TAG).get()

    init {
        planNextTrancate()
        checkPermanentAuth()
        checkPermissions()
        loadSettings()
        updateLoginState()
    }

    private fun updateLoginState() {
        viewModelScope.launch {
            tokenRepositoryImpl.loginFlow.collect { login ->
                _state.update {
                    it.copy(userLogin = login)
                }
            }
        }
    }

    fun refreshSteps() {
        viewModelScope.launch {
            _state.update { it.copy(isUserStepsLoading = true) }
            sensorsManager.refreshSteps()
            _state.update { it.copy(isUserStepsLoading = false) }
        }
    }

    private fun checkPermissions() {
        viewModelScope.launch {
            permissionManager.permissionsGranted.collect { isGranted ->
                _state.update { it.copy(isPermissionsGet = isGranted) }
            }
        }
    }

    private fun planNextTrancate() {
        logger(LoggerType.Info, "$work")
        if (work.isNullOrEmpty() || work.any { it.state == WorkInfo.State.SUCCEEDED }) {
            statsRepository.planNextTrancateSteps()
            logger(LoggerType.Info, "trancate work not found. creating a new...")
        } else {
            work.forEach { work ->
                _state.update { it.copy(trancateWorkerStatus = work) }
                logger(
                    LoggerType.Debug,
                    "work status updated: ${work.state}, next trancate in: ${
                        formatDeadline(work.nextScheduleTimeMillis)
                    }"
                )
            }
        }
    }

    //region Service
    fun userServiceCheckerListener(turnState: Boolean, context: Context) {
        if (turnState) startService(context) else stopService(context)
    }

    fun startService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        try {
            context.startForegroundService(serviceIntent)
            _state.update { it.copy(isServiceEnabledByUser = true, isServiceRunning = true) }
            saveSettings()
        } catch (e: Exception) {
            logger(LoggerType.Error, "Failed to start service: ${e.message}")
        }
    }

    private fun stopService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.stopService(serviceIntent)
        _state.update { it.copy(isServiceEnabledByUser = false, isServiceRunning = false) }
        saveSettings()
    }

//endregion

    //region Authentification
    fun logout() {
        viewModelScope.launch {
            tokenRepositoryImpl.clearToken()
            _state.update {
                it.copy(
                    isLoggedIn = false,
                    userLogin = null
                )
            }
            _transferState.update { it.copy(isSuccessful = null, errorMessage = null) }
        }
    }

    fun goOfflineUse() {
        viewModelScope.launch {
            tokenRepositoryImpl.saveOfflineToken()
            _state.update {
                it.copy(
                    isLoggedIn = true,
                    userLogin = null
                )
            }
        }
    }

    /**
     * TODO: переписать метод на withContext, а так же проверить необходимость
     * самой проверки, а так же перенести логику в репозиторий
     */
    private fun checkPermanentAuth() {
        viewModelScope.launch {
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
                    val login = tokenRepositoryImpl.getSavedLogin()
                    _state.update {
                        it.copy(isLoggedIn = true)
                    }
                    Log.d("dataTransfer", "Token is valid. Loading main screen for login: $login")
                }
            }
        }
    }

    fun sendLoginData(login: String, password: String) {
        viewModelScope.launch {
            _transferState.update { it.copy(isLoading = true) }
            val result = networkClient.sendLoginInfo(login, password)
            if (result.isSuccessful == true && result.errorMessage == null) {

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
                _state.update {
                    it.copy(
                        isLoggedIn = true,
                        userLogin = login
                    )
                }
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = true
                    )
                }
                logger(LoggerType.Debug, "success, ${state.value}")
            } else {
                logger(LoggerType.Error, "viewModel transfer error: ${result.errorMessage}")
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = result.isSuccessful,
                        errorMessage = result.errorMessage
                    )
                }
                logger(LoggerType.Info, state.value.toString())
            }
        }
    }

    fun changeLogin(login: String, newLogin: String) {
        viewModelScope.launch {
            if (networkClient.changeUserLogin(login, newLogin)) {
                tokenRepositoryImpl.saveNewLogin(newLogin)
            }
        }
    }
//endregion

    //region Settings
    fun changeTheme(currentTheme: AppThemes) {
        _state.update { it.copy(currentTheme = currentTheme) }
        saveSettings()
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
                savedIsServiceEnabled = state.value.isServiceEnabledByUser
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
                isServiceEnabledByUser = settings.savedIsServiceEnabled
            )
        }
    }
//endregion

//region Sync Data

    suspend fun syncData(login: String, steps: Int, weeklySteps: Int): PlayersListSyncData {
        return withContext(Dispatchers.IO) {
            statsRepository.syncData(login = login, steps = steps, weeklySteps = weeklySteps)
        }
    }
//endregion
}
