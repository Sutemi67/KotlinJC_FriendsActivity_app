package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.ratings

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
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
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviews
import apc.appcradle.kotlinjc_friendsactivity_app.data.StatsRepo
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import kotlinx.coroutines.async

@Composable
fun RatingsScreen() {
    val statsRepository = StatsRepo()
//    val statsRepository = koinInject<StatsRepo>()
    var list by remember { mutableStateOf<List<PlayerActivityData>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val isSynced = statsRepository.syncStatus.collectAsState().value

    LaunchedEffect(Unit) {
        val sync = scope.async { statsRepository.percentageMax() }
        sync.await()
        list = statsRepository.playersList
        Log.d("players", "data synced")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedButton(
            onClick = {}
        ) { Text("Sync data") }
        HorizontalDivider()
        Text("Players list")
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
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        RatingsScreen()
    }
}