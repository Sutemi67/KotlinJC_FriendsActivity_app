package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun RatingsScreen(
    login: String?,
    isSynced: Boolean,
    syncFun: suspend () -> PlayersListSyncData,
) {
    var errorMessage: String? by remember { mutableStateOf("") }
    var kmWeekly by remember { mutableDoubleStateOf(0.0) }
    var leaderDifference by remember { mutableDoubleStateOf(0.0) }
    var list by remember { mutableStateOf<List<PlayerActivityData>>(emptyList()) }

    LaunchedEffect(Unit) {
        if (login != null) {
            try {
                val response = syncFun()
                errorMessage = response.errorMessage
                kmWeekly = response.summaryKm
                leaderDifference = response.leaderDifferenceKm
                list = response.playersList
            } catch (e: Exception) {
                Log.e("dataTransfer", "RatingsScreen: error syncing data: ${e.message}")
                errorMessage = "${e.message}"
            }
        } else {
            list = emptyList()
            kmWeekly = 0.0
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.height(5.dp)) {
            if (isSynced)
                LinearProgressIndicator()
        }
        StatsTable(kmWeekly, leaderDifference)
        if (errorMessage == null) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(list) { index, item ->
                    PlayerStatsView(
                        index = index,
                        playerActivityData = item
                    )
                }
            }
        } else {
            AppText(
                color = Color.Red,
                text = errorMessage!!
            )
        }
    }
}

@Preview
@Composable
private fun Preview2() {
    KotlinJC_FriendsActivity_appTheme {
        RatingsScreen(
            login = "AlexMagnuss",
            isSynced = true,
            syncFun = {
                PlayersListSyncData(
                    playersList = listOf(
                        PlayerActivityData(
                            "Alex",
                            33,
                            2333,
                            .63f,
                        ),
                        PlayerActivityData(
                            "Alex",
                            333,
                            2333,
                            .3f,
                        )
                    ),
                    summaryKm = 33.0,
                    leaderDifferenceKm = 22.3,
                    errorMessage = null
                )
            },
        )
    }
}