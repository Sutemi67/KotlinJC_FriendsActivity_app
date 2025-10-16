package apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import apc.appcradle.core.constants.WORKER_TAG
import apc.appcradle.domain.models.network.PlayersListSyncData
import apc.appcradle.domain.usecases_auth.ChangeLoginUseCase
import apc.appcradle.domain.usecases_auth.CheckPermanentAuthUseCase
import apc.appcradle.domain.usecases_auth.GetTokenUseCase
import apc.appcradle.domain.usecases_auth.LogoutUseCase
import apc.appcradle.domain.usecases_auth.OfflineUseUseCase
import apc.appcradle.domain.usecases_auth.SaveNewLoginUseCase
import apc.appcradle.domain.usecases_auth.SendLoginUseCase
import apc.appcradle.domain.usecases_auth.SendRegistrationUseCase
import apc.appcradle.domain.usecases_auth.SyncDataUseCase
import apc.appcradle.kotlinjc_friendsactivity_app.NetworkAppState
import apc.appcradle.kotlinjc_friendsactivity_app.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkViewModel(
    private val permissionManager: PermissionManager,
    private val workManager: WorkManager,
    private val offlineUseUseCase: OfflineUseUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkPermanentAuthUseCase: CheckPermanentAuthUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val sendLoginUseCase: SendLoginUseCase,
    private val sendRegistrationUseCase: SendRegistrationUseCase,
    private val changeLoginUseCase: ChangeLoginUseCase,
    private val saveNewLoginUseCase: SaveNewLoginUseCase,
    private val syncDataUseCase: SyncDataUseCase
) : ViewModel() {

    private var _networkState = MutableStateFlow(NetworkAppState())
    val networkState: StateFlow<NetworkAppState> = _networkState.asStateFlow()

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
                _networkState.update { it.copy(isPermissionsGet = isGranted) }
            }
        }
    }

    fun logout() {
        logoutUseCase()
        _networkState.update { NetworkAppState() }
        Log.i("login", "logout-> ${networkState.value}")

    }

    fun goOfflineUse() {
        offlineUseUseCase()
        _networkState.update { it.copy(isLoggedIn = true, userLogin = null) }
        Log.i("login", "offline-> ${networkState.value}")

    }

    private fun checkPermanentAuth() {
        val token = checkPermanentAuthUseCase()
        when (token) {
            "offline" -> {
                _networkState.update { it.copy(isLoggedIn = true) }
            }

            null -> {
                Log.d("dataTransfer", "Permanent token is not valid...")
            }

            else -> {
                val login = getTokenUseCase()
                _networkState.update { it.copy(isLoggedIn = true, userLogin = login) }
            }
        }
    }

    fun sendLoginData(login: String, password: String) {
        viewModelScope.launch {
            _networkState.update { it.copy(isLoading = true) }
            val result = sendLoginUseCase(login, password)
            if (result.isSuccessful == true && result.errorMessage == null) {
                _networkState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        isSuccessful = result.isSuccessful,
                        errorMessage = null,
                        userLogin = login,
                    )
                }
            } else {
                _networkState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = result.isSuccessful,
                        errorMessage = result.errorMessage
                    )
                }
            }
            Log.i("login", "login->${networkState.value}")
        }
    }

    fun sendRegisterData(login: String, password: String) {
        viewModelScope.launch {
            _networkState.update { it.copy(isLoading = true) }
            val result = sendRegistrationUseCase(login, password)
            if (result.isSuccessful == true && result.errorMessage == null) {
                _networkState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userLogin = login,
                        isSuccessful = true,
                        errorMessage = null
                    )
                }
            } else {
                _networkState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = result.isSuccessful,
                        errorMessage = result.errorMessage
                    )
                }
            }
            Log.i("login", "register-> ${networkState.value}")

        }
    }

    fun changeLogin(login: String, newLogin: String) {
        viewModelScope.launch {
            _networkState.update { it.copy(isLoading = true) }
            if (changeLoginUseCase(login, newLogin)) {
                Log.i("login", "смена ника - ${true}")
                _networkState.update { it.copy(userLogin = newLogin, isLoading = false) }
                saveNewLoginUseCase(newLogin)
                return@launch
            }
            _networkState.update { it.copy(isLoading = false) }
            Log.e("login", "смена ника - ${false}")
        }
    }

    suspend fun syncData(login: String, steps: Int, weeklySteps: Int): PlayersListSyncData {
        _networkState.update { it.copy(isLoading = true) }
        return withContext(Dispatchers.IO) {
            val result = syncDataUseCase(
                login = login, steps = steps, weeklySteps = weeklySteps
            )
            Log.i("dataTransfer", "ViewModel sync result: $result")
            _networkState.update { it.copy(isLoading = false) }
            result
        }
    }
}