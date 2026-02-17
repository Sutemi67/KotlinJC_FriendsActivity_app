package apc.appcradle.kotlinjc_friendsactivity_app.features._common_components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalContext
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.StepCounterService
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsState

@Composable
fun ServiceRestartingFunc(settingsState: State<SettingsState>) {
    val isServiceRunning = settingsState.value.isServiceRunning
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (isServiceRunning) {
            val serviceIntent = Intent(context, StepCounterService::class.java)
            try {
                context.startForegroundService(serviceIntent)
                logger(LoggerType.Info, "service started in Host")
            } catch (e: Exception) {
                logger(LoggerType.Error, "Failed to start service: ${e.message}")
            }
        }
    }
}