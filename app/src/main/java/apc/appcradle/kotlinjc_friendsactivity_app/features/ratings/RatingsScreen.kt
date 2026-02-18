package apc.appcradle.kotlinjc_friendsactivity_app.features.ratings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.components.PlayerStatsView
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.components.StatsTable
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun RatingsScreen(
    vm: RatingsViewModel = koinViewModel(),
) {
    val ratingsState by vm.state.collectAsState()
    RatingsScreenUi(
        state = ratingsState,
        retrySync = { vm.obtainEvent(RatingsEvents.SyncData) }
    )
}

@Composable
fun RatingsScreenUi(
    state: RatingsState,
    retrySync: () -> Unit
) {
    LaunchedEffect(Unit) {
        retrySync()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatsTable(
            modifier = Modifier.padding(bottom = 10.dp),
            distance = state.summaryKm,
            leaderDifference = state.leaderDifferenceKm,
            leader = state.leader
        )
        AnimatedContent(
            targetState = state.isLoading,
        ) { isLoading ->
            when {
                isLoading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                state.errorMessage != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AppText(
                            modifier = Modifier.padding(20.dp),
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            text = state.errorMessage
                        )
                        // Кнопку ретрая можно добавить сюда
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        items(items = state.playersList, key = { it.login }) { element ->
                            PlayerStatsView(
                                myLogin = state.userLogin,
                                playerActivityData = element
                            )
                        }
                    }
                }
            }
        }
    }
}