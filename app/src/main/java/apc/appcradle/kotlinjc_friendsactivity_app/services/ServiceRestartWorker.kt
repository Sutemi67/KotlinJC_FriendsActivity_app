package apc.appcradle.kotlinjc_friendsactivity_app.services

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import apc.appcradle.kotlinjc_friendsactivity_app.services.StepCounterService

class ServiceRestartWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context, workerParams
) {
    override suspend fun doWork(): Result {
        return try {
            val serviceIntent = Intent(applicationContext, StepCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(serviceIntent)
            } else {
                applicationContext.startService(serviceIntent)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

fun createRestartServiceWork(): WorkRequest {
    return OneTimeWorkRequestBuilder<ServiceRestartWorker>().addTag("Restart worker").build()
}