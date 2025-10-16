package apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apc.appcradle.domain.models.local_data.SensorsDataState
import apc.appcradle.domain.usecases_sensors.GetStepsUseCase
import apc.appcradle.domain.usecases_service.CheckServiceStatusUseCase
import apc.appcradle.domain.usecases_service.StartServiceUseCase
import apc.appcradle.domain.usecases_service.StopServiceUseCase
import apc.appcradle.kotlinjc_friendsactivity_app.StepCounterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ServiceViewModel(
    private val checkServiceStatusUseCase: CheckServiceStatusUseCase,
    private val startServiceUseCase: StartServiceUseCase,
    private val stopServiceUseCase: StopServiceUseCase,
    getStepsUseCase: GetStepsUseCase,
) : ViewModel() {

    private val _isServiceWorkingState = MutableStateFlow(false)
    val isServiceWorkingState: StateFlow<Boolean> = _isServiceWorkingState.asStateFlow()

    val stepsDataState: StateFlow<SensorsDataState> = getStepsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SensorsDataState(false, 0, 0)
    )

    init {
        updateServiceState()
    }

    fun updateServiceState() {
        val isRun: Boolean = checkServiceStatusUseCase(StepCounterService::class.java)
        _isServiceWorkingState.update { isRun }
        Log.i("service", "is service running -> $isRun")
    }

    fun startService() {
        startServiceUseCase(StepCounterService::class.java)
        updateServiceState()
    }

    fun stopService() {
        stopServiceUseCase(StepCounterService::class.java)
        updateServiceState()
    }
}