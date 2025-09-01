package apc.appcradle.kotlinjc_friendsactivity_app.sensors

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import apc.appcradle.kotlinjc_friendsactivity_app.data.SettingsStorageImpl
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppSensorsManager(
    context: Context,
    private val settingsPreferencesImpl: SettingsStorage,
) : SensorEventListener {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    var isStepSensorAvailable: Boolean = (stepCounterSensor != null || stepDetectorSensor != null)
        private set

    private var _summaryStepsData = MutableStateFlow(0)
    val summaryStepsData = _summaryStepsData.asStateFlow()

    private var _dailyStepsData = MutableStateFlow(0)
    val dailyStepsData = _dailyStepsData.asStateFlow()


    private var stepsInitial = loadSteps()
    private var currentSteps = 0
    private var stepsWithoutChecking = 0
    private var isFirstStart = true
    private var isSaved = false

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
            Log.e("sensors", "No step sensor available on this device!")
        }
    }

    fun unregisterSensors() {
        stepCounterSensor?.let { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
        stepDetectorSensor?.let { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
        settingsPreferencesImpl.saveSteps(currentSteps)
    }

    private fun periodicalSaving() {
        if (!isSaved) {
            CoroutineScope(Dispatchers.IO).launch {
                isSaved = true
                delay(60000)
                settingsPreferencesImpl.saveSteps(currentSteps)
                isSaved = false
            }
        }
    }

    private fun loadSteps(): Int {
        val loadedSteps = settingsPreferencesImpl.getSteps()
        _summaryStepsData.value = loadedSteps
        return loadedSteps
    }

    private fun resetSteps() {
        stepsInitial = 0
        _summaryStepsData.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    val totalSensorSteps = sensorEvent.values[0].toInt()
                    if (isFirstStart && totalSensorSteps > 0) {
                        stepsWithoutChecking = totalSensorSteps - stepsInitial
                        isFirstStart = false
                    }
                    if (!isFirstStart) {
                        stepsCounter(totalSensorSteps)
                    }
                }

                Sensor.TYPE_STEP_DETECTOR -> {
                    currentSteps += 1
                    _summaryStepsData.value = currentSteps
                    _dailyStepsData.value++
                    periodicalSaving()
                }
            }
        }
    }

    private fun stepsCounter(totalSensorSteps: Int) {
        currentSteps = totalSensorSteps - stepsWithoutChecking
        _summaryStepsData.value = currentSteps
        _dailyStepsData.value++
        periodicalSaving()
    }
}