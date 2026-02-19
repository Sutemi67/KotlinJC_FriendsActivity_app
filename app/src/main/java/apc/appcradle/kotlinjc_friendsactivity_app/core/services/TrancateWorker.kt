package apc.appcradle.kotlinjc_friendsactivity_app.core.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

private const val KEY_TO_WORKER_BY_LOGIN = "KEY_LOGIN"

class TrancateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context, workerParams
), KoinComponent {
    private val appSensorsManager: AppSensorsManager by inject()

    override suspend fun doWork(): Result {
        val login = inputData.getString(KEY_TO_WORKER_BY_LOGIN)

        return try {
            appSensorsManager.truncate(login)
            logger(LoggerType.Info, this, "truncated successfully for $login")
            Result.success()
        } catch (e: Exception) {
            logger(LoggerType.Info, this, "truncate retry, error: ${e.message}")
            Result.retry()
        }
    }
}

fun trancateStepsRequest(delay: Long, login: String?): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<TrancateWorker>()
//        .setInitialDelay(duration = 3, timeUnit = TimeUnit.MINUTES)
        .setInitialDelay(duration = delay, timeUnit = TimeUnit.MILLISECONDS)
        .setInputData(workDataOf(KEY_TO_WORKER_BY_LOGIN to login))
        .build()
}