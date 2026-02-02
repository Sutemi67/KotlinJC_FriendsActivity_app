package apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.TokenRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.model.Steps
import apc.appcradle.kotlinjc_friendsactivity_app.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.utils.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppSensorsManager(
    context: Context,
    private val statsRepository: StatsRepository,
    private val tokenRepository: TokenRepositoryImpl
) : SensorEventListener {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var actualLogin: String? = null
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    var isStepSensorAvailable: Boolean = (stepCounterSensor != null || stepDetectorSensor != null)
        private set

    private var _allSteps = MutableStateFlow(0)
    val allSteps = _allSteps.asStateFlow()

    private var _weeklySteps = MutableStateFlow(0)
    val weeklySteps = _weeklySteps.asStateFlow()

    private var stepsInitialWeekly: Int = 0
    private var currentSteps = 0
    private var stepsWithoutChecking = 0
    private var isFirstStart = true
    private var isSavingInProgress = false
    private var lastTotalCounterSteps: Int? = null

    init {
        scope.launch {
            loggedLoadingSteps()
            tokenRepository.loginFlow.collect { login ->
                actualLogin = login
            }
        }
    }

    fun registerSensors() {
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
            logger(LoggerType.Error, "No step sensor available on this device!")
        }
    }

    fun unregisterSensors() {
        stepCounterSensor?.let { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
        stepDetectorSensor?.let { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
        statsRepository.saveAllSteps(
            steps = Steps(
                allSteps = allSteps.value,
                weeklySteps = weeklySteps.value
            ),
            login = actualLogin
        )
    }

    private fun periodicalSaving() {
        if (!isSavingInProgress) {
            scope.launch {
                isSavingInProgress = true
                statsRepository.saveAllSteps(
                    steps = Steps(
                        allSteps = allSteps.value,
                        weeklySteps = weeklySteps.value
                    ),
                    login = actualLogin
                )
                delay(60000)
                isSavingInProgress = false
            }
        }
    }

    fun trancate() {
        statsRepository.trancate()
        currentSteps = 0
        stepsWithoutChecking = 0
        isFirstStart = true
        lastTotalCounterSteps = null
        truncateLoadingSteps()
    }

    private suspend fun loggedLoadingSteps() {
        val loadedSteps = statsRepository.fetchSteps(actualLogin)
        _allSteps.value = loadedSteps.allSteps
        _weeklySteps.value = loadedSteps.weeklySteps
        stepsInitialWeekly = loadedSteps.weeklySteps
        currentSteps = loadedSteps.weeklySteps
    }

    suspend fun refreshSteps() = loggedLoadingSteps()

    private fun truncateLoadingSteps() {
        val loadedSteps = statsRepository.getLocalSteps(actualLogin)
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