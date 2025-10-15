package apc.appcradle.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import apc.appcradle.core.constants.WORKER_TAG
import java.util.concurrent.TimeUnit

class TrancateWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val appSensorsManager: AppSensorsManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        appSensorsManager.trancate()
        return Result.success()
    }
}

fun trancateStepsRequest(delay: Long) = OneTimeWorkRequestBuilder<TrancateWorker>()
    .setInitialDelay(duration = delay, timeUnit = TimeUnit.MILLISECONDS)
    .addTag(WORKER_TAG)
    .build()