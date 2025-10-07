package apc.appcradle.kotlinjc_friendsactivity_app.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.koin.java.KoinJavaComponent.inject

class BootAndRestartReceiver : BroadcastReceiver() {

    private val permissionManager by inject<PermissionManager>(PermissionManager::class.java)
    private val settingsRepository by inject<SettingsRepository>(SettingsRepository::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        val settings = settingsRepository.loadSettingsData()
        val isEnabled = settings.savedIsServiceEnabled

        if (!isEnabled) return
        if (!permissionManager.arePermissionsGranted()) return

        try {
            val serviceIntent = Intent(context, StepCounterService::class.java)
            context.startForegroundService(serviceIntent)
        } catch (e: Exception) {
            Log.e("boot", "Failed to start service on boot: ${e.message}")
        }
    }
}


