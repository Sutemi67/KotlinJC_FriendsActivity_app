package apc.appcradle.domain.usecases_sensors

import apc.appcradle.domain.SensorsManager
import apc.appcradle.domain.models.local_data.SensorsDataState
import kotlinx.coroutines.flow.StateFlow

class GetStepsUseCase(
    private val sensorsManager: SensorsManager
) {
    operator fun invoke(): StateFlow<SensorsDataState> = sensorsManager.sensorsDataFlow
}