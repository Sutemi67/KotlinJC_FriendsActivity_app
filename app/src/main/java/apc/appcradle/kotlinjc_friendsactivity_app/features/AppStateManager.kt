package apc.appcradle.kotlinjc_friendsactivity_app.features

import androidx.compose.runtime.Immutable
import androidx.work.WorkInfo
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.ITokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
class AppStateManager(
    private val tokenRepository: ITokenRepository
) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _appState = MutableStateFlow(AppState())
    val appState = _appState.asStateFlow()

    init {
        scope.launch {
            tokenRepository.tokenFlow.collect { tokenState ->
                    logger(LoggerType.Debug, "App state changed -> $tokenState")
                    _appState.update {
                        it.copy(
                            uiState = tokenState.uiState,
                            userLogin = tokenState.login
                        )
                    }
                }
        }
    }
}

data class AppState(
    //Navigation
    val currentDestination: String = Destinations.AUTH.route,

    //User
    val uiState: UiState = UiState.SPLASH,
    val userLogin: String? = null,
    val userWeeklySteps: Int = 0,
    val userAllSteps: Int = 0,

    //Trancate worker status
    val trancateWorkerStatus: WorkInfo? = null
)