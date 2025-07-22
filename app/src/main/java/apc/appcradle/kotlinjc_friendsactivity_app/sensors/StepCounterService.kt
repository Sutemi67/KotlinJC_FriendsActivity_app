package apc.appcradle.kotlinjc_friendsactivity_app.sensors

import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.koin.android.ext.android.inject

class StepCounterService() : Service() {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "StepCounterChannel"
    }

    private val sensorManager: AppSensorsManager by inject()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification())
            }
        } catch (e: ForegroundServiceStartNotAllowedException) {
            stopSelf()
            Log.i("sensors", "${e.message}")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager.startCounting()
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        sensorManager.stopCounting()
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Step Counter"
            val descriptionText = "Tracks your steps in background"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Подсчет шагов")
        .setContentText("Пройдено: ${sensorManager.stepsData.value}")
        .setSmallIcon(apc.appcradle.kotlinjc_friendsactivity_app.R.drawable.sleep)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .build()

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.stopCounting()
    }
} 