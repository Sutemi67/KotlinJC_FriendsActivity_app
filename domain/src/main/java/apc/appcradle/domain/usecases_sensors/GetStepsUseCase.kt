package apc.appcradle.domain.usecases_sensors

import apc.appcradle.domain.SensorsManager
import kotlinx.coroutines.flow.StateFlow

class GetStepsUseCase(
    sensorsManager: SensorsManager
) {
    val sensorStatus: Boolean = sensorsManager.isStepSensorAvailable
    val allSteps: StateFlow<Int> = sensorsManager.allSteps
    val weeklySteps: StateFlow<Int> = sensorsManager.weeklySteps
}