package apc.appcradle.kotlinjc_friendsactivity_app

import android.app.AlarmManager
import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.hardware.SensorEventListener
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import apc.appcradle.domain.SettingsRepository
import apc.appcradle.domain.usecases_sensors.RegisterSensorsUseCase
import apc.appcradle.domain.usecases_sensors.UnregisterSensorsUseCase
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.MainActivity
import org.koin.android.ext.android.inject

class StepCounterService(
    private val registerSensorsUseCase: RegisterSensorsUseCase,
    private val unregisterSensorsUseCase: UnregisterSensorsUseCase,
) : Service() {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "StepCounterChannel"
    }

    private val settingsRepository by inject<SettingsRepository>()
    private val permissionManager by inject<PermissionManager>()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startServiceInForeground()
        registerSensorsUseCase()
        Log.i("service", "Service -> onStartCommand")
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        scheduleSelfRestart()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.S)
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
        .setContentTitle("Friends Activity")
        .setContentText("Не сиди долго.")
        .setSmallIcon(R.drawable.outline_directions_run_24)
        .setLargeIcon(
            BitmapFactory.decodeResource(
                resources,
                R.mipmap.ic_launcher
            )
        )
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterSensorsUseCase()
        Log.i("service", "Service -> Destroyed")
    }

    private fun scheduleSelfRestart() {


        val isEnabled = try {
            settingsRepository.loadSettingsData().savedIsServiceEnabled
        } catch (e: Exception) {
            Log.e("service", "Service -> ${e.message}")
            false
        }
        if (!isEnabled || !permissionManager.arePermissionsGranted()) return
        val restartIntent = Intent(this, StepCounterService::class.java)
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            restartIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 5_000L,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("service", "Service -> ${e.message}")
        }
    }
}