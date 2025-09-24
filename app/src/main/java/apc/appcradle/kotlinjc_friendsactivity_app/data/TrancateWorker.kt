package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

const val WORKER_TAG = "trancate"

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
    .addTag(WORKER_TAG)
    .build()