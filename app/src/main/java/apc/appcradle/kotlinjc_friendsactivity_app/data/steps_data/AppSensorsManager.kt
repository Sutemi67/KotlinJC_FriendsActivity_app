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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

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

    @Volatile
    private var isDataLoaded = false

    init {
        scope.launch {
            tokenRepository.loginFlow.collect { login ->
                actualLogin = login
                logger(LoggerType.Info, "login updated: $login")
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
            isSavingInProgress = true
            scope.launch {
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
        scope.launch {
            // Ждем первое актуальное значение логина, прежде чем чистить
            val currentLogin = tokenRepository.loginFlow.first()
            statsRepository.truncate(currentLogin)
//        statsRepository.truncate(actualLogin)
            currentSteps = 0
            stepsWithoutChecking = 0
            isFirstStart = true
            lastTotalCounterSteps = null
            truncateLoadingSteps()
        }
    }

    private suspend fun loggedLoadingSteps() {
        isDataLoaded = false

        val loadedSteps = statsRepository.fetchSteps(actualLogin)
        _allSteps.value = loadedSteps.allSteps
        _weeklySteps.value = loadedSteps.weeklySteps
        stepsInitialWeekly = loadedSteps.weeklySteps
        currentSteps = loadedSteps.weeklySteps

        isFirstStart = true

        isDataLoaded = true
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
        if (!isDataLoaded) return
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    val totalSensorSteps = sensorEvent.values[0].toInt()

//                    if (isFirstStart) {
//                        stepsWithoutChecking = totalSensorSteps - stepsInitialWeekly
//                        lastTotalCounterSteps = totalSensorSteps
//                        isFirstStart = false
//                    }
//                    // Используем else, чтобы не выполнять этот блок в тот же такт, что и инициализацию
//                    // (хотя это не критично, но логичнее)
//                    else {
//                        currentSteps = totalSensorSteps - stepsWithoutChecking
//                        _weeklySteps.value = currentSteps
//
//                        val previous = lastTotalCounterSteps ?: totalSensorSteps
//                        val delta = (totalSensorSteps - previous).coerceAtLeast(0)
//                        lastTotalCounterSteps = totalSensorSteps
//
//                        if (delta > 0) {
//                            _allSteps.value += delta
//                            periodicalSaving()
//                        }
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
                    currentSteps = totalSensorSteps - stepsWithoutChecking

                    // 3. Расчет дельты для общего счетчика (allSteps)
                    val previous = lastTotalCounterSteps ?: totalSensorSteps
                    val delta = (totalSensorSteps - previous).coerceAtLeast(0)

                    if (delta > 0) {
                        _weeklySteps.value = currentSteps
                        _allSteps.value += delta
                        lastTotalCounterSteps = totalSensorSteps
                        periodicalSaving()
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