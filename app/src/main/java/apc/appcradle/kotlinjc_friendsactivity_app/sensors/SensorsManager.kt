package apc.appcradle.kotlinjc_friendsactivity_app.sensors

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSensorsManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var stepsInitial = -1
    private var currentSteps = 0
//    private var rememberedSteps = 0

    private var _stepsData = MutableStateFlow(0)
    val stepsData = _stepsData.asStateFlow()

    fun startCounting() {
        stepCounterSensor?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun stopCounting() {
        stepCounterSensor?.let { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
//        rememberedSteps = currentSteps
    }

    fun resetSteps() {
        stepsInitial = -1
//        _stepsData.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    val totalSensorSteps = sensorEvent.values[0].toInt()
                    stepsCounter(totalSensorSteps)
                }
            }
        }
    }

    private fun stepsCounter(totalSensorSteps: Int) {
        if (stepsInitial == -1) {
            stepsInitial = totalSensorSteps
        }
        currentSteps = totalSensorSteps - stepsInitial
        _stepsData.value = currentSteps
        Log.i(
            "sensors",
            "Steps detected: Total=$totalSensorSteps, Current=$currentSteps"
        )
    }
}