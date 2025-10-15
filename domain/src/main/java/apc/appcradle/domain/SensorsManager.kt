package apc.appcradle.domain

import apc.appcradle.domain.models.local_data.SensorsDataState
import kotlinx.coroutines.flow.StateFlow

interface SensorsManager {
    fun registerSensors()
    fun unregisterSensors()
    fun trancate()

    val sensorsDataFlow: StateFlow<SensorsDataState>
}