package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import apc.appcradle.kotlinjc_friendsactivity_app.domain.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.domain.StepCounterService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

const val SERVICE_RESTART_TAG = "service_restart"

fun createServiceRestartRequest(delayMillis: Long = 15_000L): WorkRequest {
    return OneTimeWorkRequestBuilder<ServiceRestartWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .addTag(SERVICE_RESTART_TAG)
        .build()
}

fun createServiceHealthCheckRequest(): WorkRequest {
    return PeriodicWorkRequestBuilder<ServiceRestartWorker>(4, TimeUnit.HOURS)
        .addTag("${SERVICE_RESTART_TAG}_health")
        .setInitialDelay(4, TimeUnit.HOURS)
        .build()
}

class ServiceRestartWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context, workerParams
), KoinComponent {

    private val settingsRepository: SettingsRepository by inject()
    private val permissionManager: PermissionManager by inject()

    override suspend fun doWork(): Result {
        return try {
            Log.i("service", "ServiceRestartWorker -> Attempting to restart service")

            // Check if service should be enabled
            val isEnabled = try {
                settingsRepository.loadSettingsData().savedIsServiceEnabled
            } catch (e: Exception) {
                Log.e("service", "ServiceRestartWorker -> Error loading settings: ${e.message}")
                false
            }

            if (!isEnabled) {
                Log.i("service", "ServiceRestartWorker -> Service is disabled in settings")
                return Result.success()
            }

            // Check permissions
            if (!permissionManager.arePermissionsGranted()) {
                Log.i("service", "ServiceRestartWorker -> Permissions not granted")
                return Result.failure(workDataOf("reason" to "permissions_not_granted"))
            }

            // Start the service
            val serviceIntent = Intent(applicationContext, StepCounterService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(serviceIntent)
            } else {
                applicationContext.startService(serviceIntent)
            }

            Log.i("service", "ServiceRestartWorker -> Service restarted successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("service", "ServiceRestartWorker -> Failed to restart service: ${e.message}")
            Result.retry() // WorkManager will handle retry with exponential backoff
        }
    }
}