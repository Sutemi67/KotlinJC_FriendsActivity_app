package apc.appcradle.kotlinjc_friendsactivity_app.core.services.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.TRANCATE_WORKER_TAG
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class TrancateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context, workerParams
), KoinComponent {
    private val appSensorsManager: AppSensorsManager by inject()

    override suspend fun doWork(): Result {
        appSensorsManager.truncate()
        return Result.success()
    }
}

fun trancateStepsRequest(delay: Long): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<TrancateWorker>()
//        .setInitialDelay(duration = 5, timeUnit = TimeUnit.MINUTES)
        .setInitialDelay(duration = delay, timeUnit = TimeUnit.MILLISECONDS)
        .addTag(TRANCATE_WORKER_TAG)
        .build()
}