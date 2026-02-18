package apc.appcradle.kotlinjc_friendsactivity_app.features

import androidx.compose.runtime.Immutable
import androidx.work.WorkInfo
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.ITokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    //User
    val uiState: UiState = UiState.SPLASH,
    val userLogin: String? = null,

    //Trancate worker status
    val trancateWorkerStatus: WorkInfo? = null
)

@Immutable
class AppStateManager(
    tokenRepository: ITokenRepository,
) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _appState = MutableStateFlow(AppState())

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
        tokenRepository.tokenFlow.onEach { (login, token, uiState) ->
            logger(LoggerType.Info, this, "appState changed")
            _appState.update { it.copy(uiState = uiState, userLogin = login) }
        }.launchIn(scope)
    }
}