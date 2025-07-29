package apc.appcradle.kotlinjc_friendsactivity_app.sensors

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import apc.appcradle.kotlinjc_friendsactivity_app.data.SettingsPreferences
import apc.appcradle.kotlinjc_friendsactivity_app.isTodayMonday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppSensorsManager(
    context: Context,
    private val settingsPreferences: SettingsPreferences
) : SensorEventListener {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var _stepsData = MutableStateFlow(0)
    val stepsData = _stepsData.asStateFlow()

    private var stepsInitial = loadSteps()
    private var currentSteps = 0
    private var stepsWithoutChecking = 0
    private var isFirstStart = true
    private var isSaved = false
    private var saveJob: Job? = null

    fun startCounting() {
        Log.d("sensors", "Starting step counting, initial steps: $stepsInitial")
        stepCounterSensor?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    private fun saving() {
        if (!isSaved) {
            isSaved = true
            CoroutineScope(Dispatchers.IO).launch {
                delay(60000)
                settingsPreferences.saveSteps(currentSteps)
                isSaved = false
            }
            Log.i(
                "sensors",
                "log from coroutine, $currentSteps"
            )
        }
    }

    fun stopCounting() {
        stepCounterSensor?.let { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
        settingsPreferences.saveSteps(currentSteps)
    }

    fun loadSteps(): Int {
        if (isTodayMonday()) {
            resetSteps()
            settingsPreferences.saveSteps(0)
            return 0
        } else {
            val steps = settingsPreferences.getSteps()
            Log.i("sensors", "Steps loaded, $steps")
            _stepsData.value = steps
            return steps
        }
    }

    fun resetSteps() {
        stepsInitial = 0
        _stepsData.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    val totalSensorSteps = sensorEvent.values[0].toInt()
                    if (isFirstStart) {
                        stepsWithoutChecking = totalSensorSteps - stepsInitial
                        isFirstStart = false
                    }
                    stepsCounter(totalSensorSteps)
                }
            }
        }
    }

    private fun stepsCounter(totalSensorSteps: Int) {
        currentSteps = totalSensorSteps - stepsWithoutChecking
        _stepsData.value = currentSteps
        saving()
    }

}