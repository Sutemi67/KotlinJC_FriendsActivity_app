package apc.appcradle.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import apc.appcradle.domain.SensorsManager
import apc.appcradle.domain.StatsRepository
import apc.appcradle.domain.models.network.Steps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppSensorsManager(
    context: Context,
    private val appStatsRepository: StatsRepository,
) : SensorEventListener, SensorsManager {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    override var isStepSensorAvailable: Boolean =
        (stepCounterSensor != null || stepDetectorSensor != null)
        private set

    private var _allSteps = MutableStateFlow(0)
    override val allSteps = _allSteps.asStateFlow()

    private var _weeklySteps = MutableStateFlow(0)
    override val weeklySteps = _weeklySteps.asStateFlow()

    private var stepsInitialWeekly: Int = 0
    private var currentSteps = 0
    private var stepsWithoutChecking = 0
    private var isFirstStart = true
    private var isSavingInProgress = false
    private var lastTotalCounterSteps: Int? = null

    init {
        loadSteps()
    }

    override fun registerSensors() {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
            return
        } else if (stepDetectorSensor != null) {
            sensorManager.registerListener(
                this,
                stepDetectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        } else {
            Log.e("sensors", "No step sensor available on this device!")
        }
    }

    override fun unregisterSensors() {
        stepCounterSensor?.let { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
        stepDetectorSensor?.let { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
        appStatsRepository.saveAllSteps(
            Steps(
                allSteps = allSteps.value,
                weeklySteps = weeklySteps.value
            )
        )
    }

    private fun periodicalSaving() {
        if (!isSavingInProgress) {
            CoroutineScope(Dispatchers.IO).launch {
                isSavingInProgress = true
                delay(60000)
                appStatsRepository.saveAllSteps(
                    Steps(
                        allSteps = allSteps.value,
                        weeklySteps = weeklySteps.value
                    )
                )
                isSavingInProgress = false
            }
        }
    }

    override fun trancate() {
        appStatsRepository.trancate()
        currentSteps = 0
        stepsWithoutChecking = 0
        isFirstStart = true
        lastTotalCounterSteps = null
        loadSteps()
    }

    private fun loadSteps() {
        val loadedSteps = appStatsRepository.loadSteps()
        _allSteps.value = loadedSteps.allSteps
        _weeklySteps.value = loadedSteps.weeklySteps
        stepsInitialWeekly = loadedSteps.weeklySteps
        currentSteps = loadedSteps.weeklySteps
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    val totalSensorSteps = sensorEvent.values[0].toInt()
                    if (isFirstStart && totalSensorSteps > 0) {
                        stepsWithoutChecking = totalSensorSteps - stepsInitialWeekly
                        isFirstStart = false
                        lastTotalCounterSteps = totalSensorSteps
                    }
                    if (!isFirstStart) {
                        currentSteps = totalSensorSteps - stepsWithoutChecking
                        _weeklySteps.value = currentSteps
                        val previous = lastTotalCounterSteps ?: totalSensorSteps
                        val delta = (totalSensorSteps - previous).coerceAtLeast(0)
                        lastTotalCounterSteps = totalSensorSteps
                        if (delta > 0) {
                            _allSteps.value += delta
                            periodicalSaving()
                        }
                    }
                }

                Sensor.TYPE_STEP_DETECTOR -> {
                    currentSteps += 1
                    _weeklySteps.value = currentSteps
                    _allSteps.value++
                    periodicalSaving()
                }
            }
        }
    }
}