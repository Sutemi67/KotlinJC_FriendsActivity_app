package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.utils.logger

private enum class ScreenState {
    Loading, Error, Success
}

@Composable
fun RatingsScreen(
    login: String?,
    syncFun: suspend () -> PlayersListSyncData,
) {
    var errorMessage: String? by remember { mutableStateOf("") }
    var leader: String? by remember { mutableStateOf("") }
    var kmWeekly by remember { mutableDoubleStateOf(0.0) }
    var leaderDifference by remember { mutableDoubleStateOf(0.0) }
    var list by remember { mutableStateOf<List<PlayerActivityData>>(emptyList()) }
    var screen by remember { mutableStateOf(ScreenState.Loading) }

    LaunchedEffect(Unit) {
        if (login != null) {
            try {
                val response = syncFun()
                errorMessage = response.errorMessage
                kmWeekly = response.summaryKm
                leaderDifference = response.leaderDifferenceKm
                list = response.playersList.filter { it.weeklySteps > 0 }
                leader = response.leader
                screen = ScreenState.Success
            } catch (e: Exception) {
                logger(LoggerType.Error, e.message ?: "Connection error")
                errorMessage = "${e.message}"
                screen = ScreenState.Error
            }
        } else {
            list = emptyList()
            kmWeekly = 0.0
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
                        modifier = Modifier.padding(20.dp), color = Color.Red, text = errorMessage!!
                    )
                }

                ScreenState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        items(list) { element ->
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

@Preview
@Composable
private fun Preview2() {
    KotlinJC_FriendsActivity_appTheme {
        RatingsScreen(
            login = "AlexMagnuss",
            syncFun = {
                PlayersListSyncData(
                    playersList = listOf(
                        PlayerActivityData(
                            "Alex",
                            33,
                            2333,
                            .4f,
                        ), PlayerActivityData(
                            "AlexMagnus",
                            33333,
                            23373,
                            .73f,
                        ), PlayerActivityData(
                            "Zero",
                            33333,
                            2,
                            .3f,
                        )
                    ).sortedByDescending { it.weeklySteps },
                    summaryKm = 33.0,
                    leaderDifferenceKm = 22.3,
                    errorMessage = null
                )
            },
        )
    }
}