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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviewsNoUi
import apc.appcradle.kotlinjc_friendsactivity_app.data.StatsRepo
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun RatingsScreen(
    login: String?,
) {
    val sensorManager = koinInject<AppSensorsManager>()
    val statsRepository = koinInject<StatsRepo>()

    var errorMessage: String? by remember { mutableStateOf("") }
    var summaryKm by remember { mutableDoubleStateOf(0.0) }
    var leaderDifference by remember { mutableDoubleStateOf(0.0) }
    var list by remember { mutableStateOf<List<PlayerActivityData>>(emptyList()) }

    val scope = rememberCoroutineScope()

    val isSynced = statsRepository.syncStatus.collectAsState().value
    val stepCount = sensorManager.stepsData.collectAsState().value


    LaunchedEffect(Unit) {
        if (login != null) {
            Log.d("dataTransfer", "Current stepCount before sync: $stepCount")
            try {
                val sync = scope.async(Dispatchers.IO) {
                    statsRepository.syncData(login = login, steps = stepCount)
                }
                errorMessage = sync.await()
                list = statsRepository.playersList

                Log.d("dataTransfer", "data synced, user=$login, steps=$stepCount")
                summaryKm = withContext(Dispatchers.Default) { calcSumSteps(list) }
                leaderDifference = withContext(Dispatchers.Default) { calcLeaderDiff(list, login) }
            } catch (e: Exception) {
                Log.e("dataTransfer", "Error syncing data: ${e.message}")
                errorMessage = "${e.message}"
            }
        } else {
            list = emptyList()
            summaryKm = 0.0
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
        StatsTable(summaryKm, leaderDifference)
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
            Text(
                color = Color.Red,
                text = errorMessage!!
            )
        }
    }
}

private fun calcSumSteps(list: List<PlayerActivityData>): Double {
    var stepsSum = 0
    if (list.isNotEmpty()) {
        list.forEach { player ->
            stepsSum += player.steps
        }
    }
    return stepsSum * 0.35 / 1000
}

private fun calcLeaderDiff(list: List<PlayerActivityData>, login: String?): Double {
    var diff = 0.0
    if (list.isNotEmpty()) {
        val leader = list.first()
        val player = list.first { it.login == login }
        diff = (leader.steps - player.steps) * 0.35 / 1000
    }
    return diff
}

@ThemePreviewsNoUi
@Composable
private fun Preview2() {
    KotlinJC_FriendsActivity_appTheme {
        RatingsScreen("AlexMagnuss")
    }
}