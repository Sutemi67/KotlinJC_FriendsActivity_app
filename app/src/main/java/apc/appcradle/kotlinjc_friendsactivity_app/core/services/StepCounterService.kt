package apc.appcradle.kotlinjc_friendsactivity_app.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import apc.appcradle.kotlinjc_friendsactivity_app.core.MainActivity
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class StepCounterService : Service() {

    private lateinit var manager: NotificationManager
    private var steps = 0
    private val sensorManager: AppSensorsManager by inject()
    private var collectionJob: Job? = null
    private var isNotifyAllowed = true

    override fun onCreate() {
        super.onCreate()
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startServiceInForeground()
        sensorManager.registerSensors()
        startStepCollection()
        logger(LoggerType.Debug, "Step counter started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startServiceInForeground() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(steps),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification(steps))
            }
        } catch (e: Exception) {
            logger(LoggerType.Error, "Failed fallback start: ${e.message}")
            stopSelf()
        }
    }

    private fun createNotificationChannel() {

        val name = getString(R.string.app_name)
        val descriptionText = "Tracks your steps in background"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        manager.createNotificationChannel(channel)
    }

    private fun startStepCollection() {
        collectionJob?.cancel()
        collectionJob = CoroutineScope(Dispatchers.Default).launch {
            sensorManager.weeklySteps.collect { value ->
                steps = value
                if (isNotifyAllowed) {
                    isNotifyAllowed = false
                    manager.notify(NOTIFICATION_ID, createNotification(steps))
                    delay(DEBOUNCE_NEW_STEPS_NOTIFIER)
                    isNotifyAllowed = true
                }
            }
        }
    }

    private fun createNotification(steps: Int): Notification {

        val openAppIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(getString(R.string.notifications_weekly_steps, steps))
            .setSmallIcon(R.drawable.outline_directions_run_24)
            .setLargeIcon(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(SENSOR_SERVICE)
            .setContentIntent(openAppIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterSensors()
        collectionJob?.cancel()
        logger(LoggerType.Error, "Step counter service destroyed")
    }

    companion object {
        const val NOTIFICATION_ID: Int = 1
        const val DEBOUNCE_NEW_STEPS_NOTIFIER: Long = 6000L
        const val CHANNEL_ID: String = "StepCounterChannel"
    }
}