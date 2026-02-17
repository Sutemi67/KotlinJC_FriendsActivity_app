package apc.appcradle.kotlinjc_friendsactivity_app.features.main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.TRANCATE_WORKER_TAG
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.formatDeadline
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.TokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.model.MainScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
class MainViewModel(
    private val permissionManager: PermissionManager,
    private val statsRepository: StatsRepository,
    private val sensorsManager: AppSensorsManager,
    tokenRepository: TokenRepository,
    workManager: WorkManager
) : ViewModel() {
    private var _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private val work: List<WorkInfo>? =
        workManager.getWorkInfosForUniqueWork(TRANCATE_WORKER_TAG).get()

    init {
        logger(LoggerType.Debug, "MainViewModel INIT: ${this.hashCode()}")
        planNextTrancate()
        checkPermissions()
        tokenRepository.tokenFlow
            .onEach { refreshSteps() }
            .launchIn(viewModelScope)
        sensorsManager.allSteps
            .onEach { all -> _state.update { it.copy(userAllSteps = all) } }
            .launchIn(viewModelScope)
        sensorsManager.weeklySteps
            .onEach { weekly -> _state.update { it.copy(userWeeklySteps = weekly) } }
            .launchIn(viewModelScope)
        sensorsManager.isStepSensorAvailable.onEach { isReady ->
            _state.update { it.copy(isSensorsAvailable = isReady) }
        }
            .launchIn(viewModelScope)
    }

    fun refreshSteps() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            sensorsManager.refreshSteps()
            _state.update { it.copy(isLoading = false) }
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
}