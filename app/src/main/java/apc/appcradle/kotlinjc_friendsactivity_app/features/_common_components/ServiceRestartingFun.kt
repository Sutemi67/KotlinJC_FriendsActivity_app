package apc.appcradle.kotlinjc_friendsactivity_app.features._common_components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.StepCounterService
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsState

@Composable
fun ServiceRestartingFunc(settingsState: State<SettingsState>) {
    val isServiceRunning by StepCounterService.isRunning.collectAsStateWithLifecycle()
    val serviceSavedOption = settingsState.value.serviceSavedOption
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        when {
            !isServiceRunning && serviceSavedOption -> {
                val serviceIntent = Intent(context, StepCounterService::class.java)
                context.startForegroundService(serviceIntent)
                logger(LoggerType.Error, "ServiceRestartingFunc", "service started")
            }

            isServiceRunning && serviceSavedOption -> {
                logger(LoggerType.Error, "ServiceRestartingFunc", "service already working")
            }

            !isServiceRunning && !serviceSavedOption -> {
                logger(LoggerType.Error, "ServiceRestartingFunc", "all service settings is off")
            }

            else -> {
                logger(LoggerType.Error, "ServiceRestartingFunc", "service restarting error")
            }
        }
    }
}