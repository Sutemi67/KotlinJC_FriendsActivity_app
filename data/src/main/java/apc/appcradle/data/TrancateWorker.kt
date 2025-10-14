package apc.appcradle.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

const val WORKER_TAG = "trancate"

class TrancateWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val sensorsManager: SensorsManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        sensorsManager.trancate()
        return Result.success()
    }
}

fun trancateStepsRequest(delay: Long) = OneTimeWorkRequestBuilder<TrancateWorker>()
    .setInitialDelay(duration = delay, timeUnit = TimeUnit.MILLISECONDS)
    .addTag(WORKER_TAG)
    .build()