package apc.appcradle.kotlinjc_friendsactivity_app.domain

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.MainActivity
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.data.SERVICE_RESTART_TAG
import apc.appcradle.kotlinjc_friendsactivity_app.data.SensorsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class StepCounterService : Service() {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "StepCounterChannel"
    }

    private val sensorManager: SensorsManager by inject()
    private val workManager: WorkManager by inject()
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var stepsUpdateJob: Job = Job()
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startServiceInForeground()
        sensorManager.registerSensors()
        
        stepsUpdateJob.cancel()
        
        stepsUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            sensorManager.allSteps.collect { steps ->
                updateNotification(steps)
            }
        }
        
        // Schedule health check to monitor service status
        scheduleServiceHealthCheck()
        
        Log.i("service", "Service -> onStartCommand")
        return START_STICKY
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        scheduleSelfRestart()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startServiceInForeground() {
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
        } catch (e: Exception) {
            when (e) {
                is ForegroundServiceStartNotAllowedException -> {
                    Log.e("service", "Failed to start foreground service: ${e.message}")
                    stopSelf()
                }

                else -> {
                    Log.e("service", "Unknown error starting service: ${e.message}")
                    try {
                        startForeground(NOTIFICATION_ID, createNotification())
                    } catch (e2: Exception) {
                        Log.e("service", "Failed fallback start: ${e2.message}")
                        stopSelf()
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = "Tracks your steps in background"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_SECRET
                // Для Samsung и других устройств - делаем уведомление постоянным
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setImportance(NotificationManager.IMPORTANCE_LOW)
                }
            }
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return createNotificationBuilder(0).build()
    }
    
    private fun createNotificationBuilder(steps: Int): NotificationCompat.Builder {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(resources.getString(R.string.steps_notification_format, steps))
            .setSmallIcon(R.drawable.outline_directions_run_24)
            .setLargeIcon(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        
        this.notificationBuilder = notificationBuilder
        return notificationBuilder
    }
    
    private fun updateNotification(steps: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = createNotificationBuilder(steps)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterSensors()
        stepsUpdateJob.cancel()
        Log.i("service", "Service -> Destroyed")
    }

    private fun scheduleSelfRestart() {
        Log.i("service", "Service -> Scheduling restart with WorkManager")
        
        try {
            val restartRequest = apc.appcradle.kotlinjc_friendsactivity_app.data.createServiceRestartRequest()
            workManager.enqueue(restartRequest)
            Log.i("service", "Service -> Restart scheduled successfully")
        } catch (e: Exception) {
            Log.e("service", "Service -> Failed to schedule restart: ${e.message}")
        }
    }

    private fun scheduleServiceHealthCheck() {
        Log.i("service", "Service -> Scheduling health check with WorkManager")
        
        try {
            val healthCheckRequest = apc.appcradle.kotlinjc_friendsactivity_app.data.createServiceHealthCheckRequest()
            workManager.enqueue(healthCheckRequest)
            Log.i("service", "Service -> Health check scheduled successfully")
        } catch (e: Exception) {
            Log.e("service", "Service -> Failed to schedule health check: ${e.message}")
        }
    }
}