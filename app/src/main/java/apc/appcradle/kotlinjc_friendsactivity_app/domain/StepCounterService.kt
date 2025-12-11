package apc.appcradle.kotlinjc_friendsactivity_app.domain

import android.app.AlarmManager
import android.app.ForegroundServiceStartNotAllowedException
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
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.MainActivity
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.data.SensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.data.createRestartServiceWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class StepCounterService : Service() {

    private lateinit var manager: NotificationManager
    private var steps = 0
    private val sensorManager: SensorsManager by inject()
    private val workManager: WorkManager by inject()
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
        Log.i("service", "Service -> onStartCommand")
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
//        scheduleAlarmSelfRestart()
        workManager.enqueue(createRestartServiceWork())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startServiceInForeground() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(steps),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification(steps))
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
                        startForeground(NOTIFICATION_ID, createNotification(steps))
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
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun startStepCollection() {
        collectionJob?.cancel()
        collectionJob = CoroutineScope(Dispatchers.Default).launch {
            sensorManager.weeklySteps.collect { value ->
                steps = value
                if (isNotifyAllowed) {
                    isNotifyAllowed = false
                    manager.notify(NOTIFICATION_ID, createNotification(steps))
                    delay(DEBOUNCE_NOTIFY)
                    isNotifyAllowed = true
                }
            }
        }
    }

    private fun createNotification(steps: Int): Notification {

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(getString(R.string.notifications_weekly_steps, steps))
            .setSmallIcon(R.drawable.outline_directions_run_24)
            .setLargeIcon(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java).apply {
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterSensors()
        collectionJob?.cancel()
        Log.i("service", "Service -> Destroyed")
    }

//    private fun scheduleAlarmSelfRestart() {
//        val settingsRepository by inject<SettingsRepository>(SettingsRepository::class.java)
//        val permissionManager by inject<PermissionManager>(PermissionManager::class.java)
//
//        val isEnabled = try {
//            settingsRepository.loadSettingsData().savedIsServiceEnabled
//        } catch (e: Exception) {
//            Log.e("service", "Service -> ${e.message}")
//            false
//        }
//        if (!isEnabled || !permissionManager.arePermissionsGranted()) return
//        val restartIntent = Intent(this, StepCounterService::class.java)
//        val pendingIntent = PendingIntent.getService(
//            this,
//            0,
//            restartIntent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
//        )
//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        try {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + 5_000L,
//                pendingIntent
//            )
//        } catch (e: SecurityException) {
//            Log.e("service", "Service -> ${e.message}")
//        }
//    }

    companion object {
        const val NOTIFICATION_ID: Int = 1
        const val DEBOUNCE_NOTIFY: Long = 6000L
        const val CHANNEL_ID: String = "StepCounterChannel"
    }
}