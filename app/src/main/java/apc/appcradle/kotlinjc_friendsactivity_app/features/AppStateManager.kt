package apc.appcradle.kotlinjc_friendsactivity_app.features

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.work.WorkInfo
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.UiState
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.ITokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.StatsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@Immutable
data class AppState(
    val uiState: UiState = UiState.SPLASH,
    val userLogin: String? = null,
    val trancateWorkerStatus: WorkInfo? = null
)

@Stable
class AppStateManager(
    tokenRepository: ITokenRepository,
    private val statsRepository: StatsRepository,
    private val workManager: WorkManager
) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _appState = MutableStateFlow(AppState())
    private var observerJob: Job? = null

    val uiState = _appState.map { it.uiState }
        .distinctUntilChanged()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.SPLASH
        )

    val workerStatus = _appState.map { it.trancateWorkerStatus }
        .distinctUntilChanged()
        .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)

    val userLogin = _appState.map { it.userLogin }
        .distinctUntilChanged()
        .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)

    init {
        tokenRepository.tokenFlow
            .onEach { (login, token, uiState) ->
                logger(LoggerType.Info, this, "New auth event: $login, $uiState")

                // 1. Сначала всегда обновляем состояние для UI
                _appState.update { it.copy(uiState = uiState, userLogin = login) }

                // 2. Сразу проверяем условия для воркера
                if (uiState == UiState.SPLASH) return@onEach

                if (login == null) {
                    logger(LoggerType.Debug, this, "Worker skipped: not logged in and not offline")
                    return@onEach
                }

                // 3. Если всё ок — запускаем логику воркеров
                statsRepository.planNextTrancateSteps(login)
                observeWorkerStatus(login)
            }
            .launchIn(scope)
    }

    private fun observeWorkerStatus(login: String?) {
        observerJob?.cancel()
        val workName = "truncate_work_$login"
        observerJob = workManager.getWorkInfosForUniqueWorkFlow(workName)
            .map { it.firstOrNull() }
            .onEach { info ->
                logger(LoggerType.Debug, this, "workers info status: $info")
                _appState.update { it.copy(trancateWorkerStatus = info) }
                if (info?.state == WorkInfo.State.SUCCEEDED) {
                    statsRepository.planNextTrancateSteps(login)
                }
            }
            .launchIn(scope)
    }
}