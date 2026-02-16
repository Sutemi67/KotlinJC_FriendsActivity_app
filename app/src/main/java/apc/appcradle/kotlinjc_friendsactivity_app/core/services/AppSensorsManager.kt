package apc.appcradle.kotlinjc_friendsactivity_app.core.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.AppStateManager
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.TokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.Steps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class AppSensorsManager(
    context: Context,
    private val statsRepository: StatsRepository,
    private val appStateManager: AppStateManager
) : SensorEventListener {
    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
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
    private var currentSteps = AtomicInteger(0)
    private var stepsWithoutChecking = 0
    private var isFirstStart = true
    private var lastTotalCounterSteps: Int? = null

    private var refreshJob: Job? = null

    @Volatile
    private var isSavingInProgress = false

    @Volatile
    private var isDataLoaded = false

    init {
        scopeIO.launch {
            appStateManager.appState.collect { state ->
                actualLogin = state.userLogin
                logger(LoggerType.Info, "login updated: $actualLogin")
                loggedLoadingSteps()
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
            logger(LoggerType.Error, "step counter initialized!")
            return
        } else if (stepDetectorSensor != null) {
            sensorManager.registerListener(
                this,
                stepDetectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
            logger(LoggerType.Error, "step detector initialized!")
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
            isSavingInProgress = true
            scopeIO.launch {
                statsRepository.saveAllSteps(
                    steps = Steps(
                        allSteps = allSteps.value,
                        weeklySteps = weeklySteps.value
                    ),
                    login = actualLogin
                )
                delay(30.seconds)
                isSavingInProgress = false
            }
        }
    }

    fun truncate() {
        scopeIO.launch {
            // Ждем первое актуальное значение логина, прежде чем чистить
            val currentLogin = appStateManager.appState.value.userLogin
            statsRepository.truncate(currentLogin)
            currentSteps.set(0)
            stepsWithoutChecking = 0
            isFirstStart = true
            lastTotalCounterSteps = null
            truncateLoadingSteps()
        }
    }

    private suspend fun loggedLoadingSteps() = withContext(Dispatchers.IO) {
        isDataLoaded = false

        val loadedSteps = statsRepository.fetchSteps(actualLogin)
        _allSteps.value = loadedSteps.allSteps
        _weeklySteps.value = loadedSteps.weeklySteps
        stepsInitialWeekly = loadedSteps.weeklySteps
        currentSteps.set(loadedSteps.weeklySteps)

        isFirstStart = true

        isDataLoaded = true
    }

    suspend fun refreshSteps() {
        refreshJob?.cancel()
        loggedLoadingSteps()
    }

    private fun truncateLoadingSteps() {
        scopeIO.launch {
            val loadedSteps = statsRepository.getLocalSteps(actualLogin)
            _allSteps.value = loadedSteps.allSteps
            _weeklySteps.value = loadedSteps.weeklySteps
            stepsInitialWeekly = loadedSteps.weeklySteps
            currentSteps.set(loadedSteps.weeklySteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent?) {
        if (!isDataLoaded) return
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    val totalSensorSteps = sensorEvent.values[0].toInt()

                    // 1. Проверка на перезагрузку устройства или первый запуск
                    // Если текущее значение датчика меньше, чем то, что мы видели в последний раз,
                    // значит счетчик обнулился системой.
                    if (isFirstStart || (lastTotalCounterSteps != null && totalSensorSteps < lastTotalCounterSteps!!)) {

                        // Пересчитываем оффсет:
                        // Мы хотим, чтобы текущие недельные шаги остались прежними,
                        // поэтому оффсет теперь равен новому значению датчика минус накопленные шаги.
                        stepsWithoutChecking = totalSensorSteps - _weeklySteps.value
                        lastTotalCounterSteps = totalSensorSteps
                        isFirstStart = false
                        return // Выходим, чтобы не считать шаги в этом цикле
                    }

                    // 2. Расчет текущих шагов за неделю
                    currentSteps.set(totalSensorSteps - stepsWithoutChecking)

                    // 3. Расчет дельты для общего счетчика (allSteps)
                    val previous = lastTotalCounterSteps ?: totalSensorSteps
                    val delta = (totalSensorSteps - previous).coerceAtLeast(0)

                    if (delta > 0) {
                        _weeklySteps.update { currentSteps.get() }
                        _allSteps.update { it + delta }
                        lastTotalCounterSteps = totalSensorSteps
                        periodicalSaving()
                    }
                }

                Sensor.TYPE_STEP_DETECTOR -> {
                    currentSteps.getAndAdd(1)
                    _weeklySteps.update { currentSteps.get() }
                    _allSteps.update { it + 1 }
                    periodicalSaving()
                }
            }
        }
    }
}