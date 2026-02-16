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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.features.AppStateManager
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.components.PlayerStatsView
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.components.StatsTable
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.models.RatingsEvents
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.PlayersListSyncData
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private enum class ScreenState {
    Loading, Error, Success
}

@Composable
fun RatingsScreen(
    vm: RatingsViewModel = koinViewModel(),
    appStateManager: AppStateManager = koinInject()
) {
    val appState = appStateManager.appState.collectAsState().value
    val ratingsState = vm.state.collectAsState().value
    RatingsScreenUi(
        login = appState.userLogin,
        list = ratingsState.list,
        syncFun = { vm.obtainEvent(RatingsEvents.SyncData) }
    )
}

@Composable
fun RatingsScreenUi(
    login: String?,
    list: PlayersListSyncData,
    syncFun: suspend () -> Unit,
) {
    var errorMessage: String? by remember { mutableStateOf("") }
    var leader: String? by remember { mutableStateOf("") }
    var kmWeekly by remember { mutableDoubleStateOf(0.0) }
    var leaderDifference by remember { mutableDoubleStateOf(0.0) }
    var screen by remember { mutableStateOf(ScreenState.Loading) }

    LaunchedEffect(Unit) {
        if (login != null) {
            syncFun()
            errorMessage = list.errorMessage
            kmWeekly = list.summaryKm
            leaderDifference = list.leaderDifferenceKm
            leader = list.leader
            screen = if (list.errorMessage != null) ScreenState.Error else ScreenState.Success
        } else {
            screen = ScreenState.Success
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatsTable(
            modifier = Modifier.padding(bottom = 10.dp),
            distance = kmWeekly,
            leaderDifference = leaderDifference,
            leader = leader
        )
        AnimatedContent(
            targetState = screen,
        ) { screen ->
            when (screen) {

                ScreenState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                ScreenState.Error -> {
                    AppText(
                        modifier = Modifier.padding(20.dp),
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        text = errorMessage ?: "Error has occurred."
                    )
                }

                ScreenState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        items(list.playersList) { element ->
                            PlayerStatsView(
                                login = login, playerActivityData = element
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//private fun Preview2() {
//    KotlinJC_FriendsActivity_appTheme {
//        RatingsScreenUi(
//            login = "AlexMagnuss",
//            syncFun = {
//                PlayersListSyncData(
//                    playersList = listOf(
//                        PlayerActivityData(
//                            "Alex",
//                            33,
//                            2333,
//                            .4f,
//                        ), PlayerActivityData(
//                            "AlexMagnus",
//                            33333,
//                            23373,
//                            .73f,
//                        ), PlayerActivityData(
//                            "Zero",
//                            33333,
//                            2,
//                            .3f,
//                        )
//                    ).sortedByDescending { it.weeklySteps },
//                    summaryKm = 33.0,
//                    leaderDifferenceKm = 22.3,
//                    errorMessage = null
//                )
//            },
//        )
//    }
//}