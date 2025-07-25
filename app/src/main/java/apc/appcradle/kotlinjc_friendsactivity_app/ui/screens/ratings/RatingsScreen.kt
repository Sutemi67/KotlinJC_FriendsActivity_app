package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviewsNoUi
import apc.appcradle.kotlinjc_friendsactivity_app.data.StatsRepo
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import kotlinx.coroutines.async
import org.koin.compose.koinInject

@Composable
fun RatingsScreen(
    login: String?,
    sensorManager: AppSensorsManager = koinInject<AppSensorsManager>()
) {
    val statsRepository = koinInject<StatsRepo>()
    var list by remember { mutableStateOf<List<PlayerActivityData>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val isSynced = statsRepository.syncStatus.collectAsState().value
    val stepCount = sensorManager.stepsData.collectAsState().value
    var errorMessage: String? by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (login != null) {
            Log.d("dataTransfer", "Current stepCount before sync: $stepCount")
            try {
                val sync =
                    scope.async { statsRepository.syncData(login = login, steps = stepCount) }
                errorMessage = sync.await()
                list = statsRepository.playersList
                Log.d("dataTransfer", "data synced, user=$login, steps=$stepCount")
            } catch (e: Exception) {
                Log.e("dataTransfer", "Error syncing data: ${e.message}")
                errorMessage = "Error syncing data: ${e.message}"
            }
        } else {
            list = emptyList()
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
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = "Players stats",
            fontSize = 20.sp
        )
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
            Text(errorMessage!!)
        }
    }
}

@ThemePreviewsNoUi
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        RatingsScreen("AlexMagnuss")
    }
}