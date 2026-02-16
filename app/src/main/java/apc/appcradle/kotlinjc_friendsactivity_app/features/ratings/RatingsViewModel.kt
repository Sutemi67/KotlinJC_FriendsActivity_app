package apc.appcradle.kotlinjc_friendsactivity_app.features.ratings

import androidx.compose.runtime.Immutable
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.features.AppStateManager
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsActions
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsState
import kotlinx.coroutines.flow.update

@Immutable
class RatingsViewModel(
    private val statsRepository: StatsRepository,
    appStateManager: AppStateManager
) :
    BaseViewModel<RatingsState, RatingsEvents, RatingsActions>(
        initialState = RatingsState()
    ) {

    val appState = appStateManager.appState.value

    override fun obtainEvent(event: RatingsEvents) {
        when (event) {
            is RatingsEvents.SyncData -> {
                mutableState.update { it.copy(isLoading = true) }
                if (appState.userLogin != null)
                    runSafely(
                        block = {
                            val list = statsRepository.syncData(
                                login = appState.userLogin,
                                steps = appState.userAllSteps,
                                weeklySteps = appState.userWeeklySteps
                            )
                            mutableState.update { it.copy(list = list) }
                        })
                mutableState.update { it.copy(isLoading = false) }
            }
        }
    }
}