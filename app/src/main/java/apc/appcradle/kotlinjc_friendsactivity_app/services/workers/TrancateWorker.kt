package apc.appcradle.kotlinjc_friendsactivity_app.services.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.SensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.utils.TRANCATE_WORKER_TAG
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class TrancateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context, workerParams
), KoinComponent {
    private val sensorsManager: SensorsManager by inject()

    override suspend fun doWork(): Result {
        sensorsManager.trancate()
        return Result.success()
    }
}

fun trancateStepsRequest(delay: Long) = OneTimeWorkRequestBuilder<TrancateWorker>()
    .setInitialDelay(duration = delay, timeUnit = TimeUnit.MILLISECONDS)
    .addTag(TRANCATE_WORKER_TAG)
    .build()