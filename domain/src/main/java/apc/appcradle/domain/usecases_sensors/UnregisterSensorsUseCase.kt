package apc.appcradle.domain.usecases_sensors

import apc.appcradle.domain.SensorsManager

class UnregisterSensorsUseCase(
    private val sensorsManager: SensorsManager
) {
    operator fun invoke() = sensorsManager.unregisterSensors()
}