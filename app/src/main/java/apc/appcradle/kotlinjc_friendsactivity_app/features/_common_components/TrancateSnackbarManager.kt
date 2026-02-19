package apc.appcradle.kotlinjc_friendsactivity_app.features._common_components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkInfo
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.formatDeadline
import apc.appcradle.kotlinjc_friendsactivity_app.features.AppStateManager

@Composable
fun TrancateSnackBarManager(
    appStateManager: AppStateManager,
    snackbarHostState: SnackbarHostState
) {
    val workerStatus by appStateManager.workerStatus.collectAsStateWithLifecycle()
    val login by appStateManager.userLogin.collectAsStateWithLifecycle()

    LaunchedEffect(workerStatus, login) {
        when {
            workerStatus == null || workerStatus?.state == WorkInfo.State.SUCCEEDED -> {
                snackbarHostState.showSnackbar(
                    message = "Участие в подведении итогов запланировано на следующее воскресенье!\n"
                )
            }

            workerStatus?.state == WorkInfo.State.ENQUEUED -> {
                snackbarHostState.showSnackbar(
                    message = "Статус подведения итогов: Запланировано.\nОсталось: ${
                        formatDeadline(
                            workerStatus!!.nextScheduleTimeMillis
                        )
                    }"
                )
            }

            else -> {
                snackbarHostState.showSnackbar(
                    message = "Статус обнуления: ${workerStatus?.state}}"
                )
            }
        }
    }
}