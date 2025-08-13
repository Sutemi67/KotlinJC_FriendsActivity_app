package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.util.Log
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayersListSyncData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatsRepo(
    private val networkClient: NetworkClient
) {
    private val _syncStatus = MutableStateFlow(false)
    val syncStatus: StateFlow<Boolean> = _syncStatus.asStateFlow()

    private var playersList = mutableListOf(
        PlayerActivityData("Alexander", 4324, 0f),
        PlayerActivityData("Maria", 343, 0f),
        PlayerActivityData("Nicolas", 43242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
    )


//    val playersList: StateFlow<List<PlayerActivityData>> = _playersList.asStateFlow()

    fun percentageMax() {
        _syncStatus.update { true }
        var maxSteps = 0
        playersList.forEach { it ->
            if (it.steps > maxSteps) maxSteps = it.steps
        }
        playersList.forEach { it ->
            it.percentage = it.steps.toFloat() / maxSteps
        }
        playersList.sortByDescending { it.percentage }
        _syncStatus.update { false }
        Log.d("dataTransfer", "StatsRepo sorted list $")
    }

    suspend fun syncData(login: String, steps: Int): PlayersListSyncData {
        _syncStatus.update { true }
        try {
            Log.d("dataTransfer", "StatsRepo syncData called with steps: $steps")
            val data = networkClient.postUserDataAndSyncFriendsData(login, steps)
            val newPlayersList = mutableListOf<PlayerActivityData>()
            data.friendsList.forEach { it ->
                newPlayersList.add(
                    PlayerActivityData(
                        login = it.login,
                        steps = it.steps,
                        percentage = 0f
                    )
                )
            }
            playersList = newPlayersList
            Log.i("dataTransfer", "StatsRepo players list: $playersList")
            percentageMax()
            val sumKm = calcSumKm()
            val difference = calcLeaderDiff(login)
            _syncStatus.update { false }
            Log.i("dataTransfer", "StatsRepo error: ${data.errorMessage}")
            return PlayersListSyncData(
                playersList = playersList,
                summaryKm = sumKm,
                leaderDifferenceKm = difference,
                errorMessage = data.errorMessage
            )
        } catch (e: Exception) {
            return PlayersListSyncData(
                errorMessage = e.message
            )
        }
    }

    private fun calcSumKm(): Double {
        var stepsSum = 0
        if (playersList.isNotEmpty()) {
            playersList.forEach { player ->
                stepsSum += player.steps
            }
        }
        Log.e("dataTransfer", "summ of steps is $stepsSum")
        return stepsSum * 0.35 / 1000
    }

    private fun calcLeaderDiff(login: String?): Double {
        var diff = 0.0
        if (playersList.isNotEmpty()) {
            val leader = playersList.first()
            val player = playersList.first { it.login == login }
            diff = (leader.steps - player.steps) * 0.35 / 1000
        }
        return diff
    }
}