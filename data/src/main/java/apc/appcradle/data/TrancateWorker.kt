package apc.appcradle.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import apc.appcradle.core.constants.WORKER_TAG
import apc.appcradle.core.utils_functions.appendLogsToFile
import java.util.concurrent.TimeUnit

class TrancateWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val appSensorsManager: AppSensorsManager
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        appSensorsManager.trancate()
        appendLogsToFile(context)
        return Result.success()
    }
}

fun trancateStepsRequest(delay: Long) = OneTimeWorkRequestBuilder<TrancateWorker>()
    .setInitialDelay(duration = delay, timeUnit = TimeUnit.MILLISECONDS)
    .addTag(WORKER_TAG)
    .build()