package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AppSensorsManager(context: Context) : SensorEventListener {

    val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val sensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        TODO("Not yet implemented")
    }
}