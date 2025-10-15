package apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import apc.appcradle.core.constants.WORKER_TAG
import apc.appcradle.domain.models.network.DataTransferState
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

//        viewModelScope.launch {
//            permissionManager.permissionsGranted.collect { isGranted ->
//                _networkState.update { it.copy(isPermissionsGet = isGranted) }
//            }
//        }
    }

    fun logout() {
        logoutUseCase()
        _networkState.update {
            it.copy(
                isLoggedIn = false,
                userLogin = null
            )
        }
        _transferState.update { it.copy(isSuccessful = null, errorMessage = null) }
    }

    fun goOfflineUse() {
        offlineUseUseCase()
        _networkState.update {
            it.copy(
                isLoggedIn = true,
                userLogin = null
            )
        }
    }

    private fun checkPermanentAuth() {
        val token = checkPermanentAuthUseCase()
        when (token) {
            "offline" -> {
                _networkState.update {
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
                val login = getTokenUseCase()
                _networkState.update {
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
            val result = sendLoginUseCase(login, password)
            if (result.isSuccessful == true && result.errorMessage == null) {
                Log.i("dataTransfer", "viewModel transfer - OK")
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = true,
                        errorMessage = null
                    )
                }
                _networkState.update {
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
            val result = sendRegistrationUseCase(login, password)
            if (result.isSuccessful == true && result.errorMessage == null) {
                Log.i("dataTransfer", "viewModel transfer - OK")
                _transferState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = true,
                        errorMessage = null
                    )
                }
                _networkState.update {
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
            if (changeLoginUseCase(login, newLogin)) {
                Log.i("dataTransfer", "смена ника - ${true}")
                _networkState.update { it.copy(userLogin = newLogin) }
                saveNewLoginUseCase(newLogin)
                return@launch
            }
            Log.e("dataTransfer", "смена ника - ${false}")
        }
    }

    suspend fun syncData(login: String, steps: Int, weeklySteps: Int): PlayersListSyncData {
        return withContext(Dispatchers.IO) {
            val result = syncDataUseCase(
                login = login, steps = steps, weeklySteps = weeklySteps
            )
            Log.i("dataTransfer", "ViewModel sync result: $result")
            result
        }
    }
}