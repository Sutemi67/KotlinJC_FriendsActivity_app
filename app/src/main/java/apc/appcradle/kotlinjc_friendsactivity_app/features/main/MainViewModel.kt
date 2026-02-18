package apc.appcradle.kotlinjc_friendsactivity_app.features.main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.model.MainScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
class MainViewModel(
    private val permissionManager: PermissionManager,
    private val sensorsManager: AppSensorsManager,
) : ViewModel() {
    private var _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    init {
        logger(LoggerType.Debug, this, "init: ${this.hashCode()}")
        checkPermissions()
        combine(
            sensorsManager.allSteps,
            sensorsManager.weeklySteps,
            sensorsManager.isLoading,
            sensorsManager.isStepSensorAvailable
        ) { all, weekly, loading, ready ->
            _state.update {
                it.copy(
                    userAllSteps = all,
                    userWeeklySteps = weekly,
                    isLoading = loading,
                    isSensorsAvailable = ready
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun checkPermissions() {
        viewModelScope.launch {
            permissionManager.permissionsGranted.collect { isGranted ->
                _state.update { it.copy(isPermissionsGet = isGranted) }
            }
        }
    }
}