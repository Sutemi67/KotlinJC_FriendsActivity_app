package apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import apc.appcradle.domain.usecases_service.CheckServiceStatusUseCase
import apc.appcradle.domain.usecases_service.StartServiceUseCase
import apc.appcradle.domain.usecases_service.StopServiceUseCase
import apc.appcradle.kotlinjc_friendsactivity_app.StepCounterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServiceViewModel(
    private val checkServiceStatusUseCase: CheckServiceStatusUseCase,
    private val startServiceUseCase: StartServiceUseCase,
    private val stopServiceUseCase: StopServiceUseCase,
) : ViewModel() {

    private val _isServiceWorkingState = MutableStateFlow(false)
    val isServiceWorkingState: StateFlow<Boolean> = _isServiceWorkingState.asStateFlow()

    init {
        updateServiceState()
    }

    fun updateServiceState() {
        val isRun: Boolean = checkServiceStatusUseCase(StepCounterService::class.java)
        _isServiceWorkingState.update { isRun }
    }

    fun startService(context: Context) {
        startServiceUseCase(StepCounterService::class.java)
        updateServiceState()
    }

    fun stopService(context: Context) {
        stopServiceUseCase(StepCounterService::class.java)
        updateServiceState()
    }
}