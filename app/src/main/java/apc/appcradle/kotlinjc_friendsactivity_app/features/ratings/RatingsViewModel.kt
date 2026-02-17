package apc.appcradle.kotlinjc_friendsactivity_app.features.ratings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.AppStateManager
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsActions
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
class RatingsViewModel(
    private val statsRepository: StatsRepository,
    private val appStateManager: AppStateManager,
    private val appSensorsManager: AppSensorsManager
) :
    BaseViewModel<RatingsState, RatingsEvents, RatingsActions>(
        initialState = RatingsState()
    ) {
    private var allSteps = 0
    private var weeklySteps = 0

    init {
        viewModelScope.launch {
            appSensorsManager.allSteps.collect { allSteps = it }
        }
        viewModelScope.launch {
            appSensorsManager.weeklySteps.collect { weeklySteps = it }
        }
        viewModelScope.launch {
            appStateManager.userLogin.collect { login ->
                mutableState.update { it.copy(userLogin = login) }
                if (login != null) {
                    obtainEvent(RatingsEvents.SyncData)
                }
            }
        }
    }

    override fun obtainEvent(event: RatingsEvents) {
        when (event) {
            is RatingsEvents.SyncData -> syncRatingsData()
        }
    }

    private fun syncRatingsData() {
        val login = state.value.userLogin ?: return
        logger(LoggerType.Debug, "Sync data in SettingsVM")
        viewModelScope.launch {
            try {
                mutableState.update { it.copy(isLoading = true, errorMessage = null) }

                val result = statsRepository.syncData(
                    login = login,
                    steps = allSteps,
                    weeklySteps = weeklySteps
                )

                mutableState.update {
                    it.copy(
                        isLoading = false,
                        playersList = result.playersList,
                        summaryKm = result.summaryKm,
                        leaderDifferenceKm = result.leaderDifferenceKm,
                        leader = result.leader,
                        errorMessage = result.errorMessage
                    )
                }
            } catch (e: Exception) {
                mutableState.update {
                    it.copy(isLoading = false, errorMessage = e.localizedMessage)
                }
            }
        }
    }
}
