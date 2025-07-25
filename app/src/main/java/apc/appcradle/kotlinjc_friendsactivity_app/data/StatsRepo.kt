package apc.appcradle.kotlinjc_friendsactivity_app.data

import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.util.Log

class StatsRepo(
    private val networkClient: NetworkClient
) {
    private val _syncStatus = MutableStateFlow(false)
    val syncStatus: StateFlow<Boolean> = _syncStatus.asStateFlow()

    var playersList = mutableListOf(
        PlayerActivityData("Alexander", 4324, 0f),
        PlayerActivityData("Maria", 343, 0f),
        PlayerActivityData("Nicolas", 43242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Andrew", 13242, 0f),
        PlayerActivityData("Maike", 11322, 0f),
    )

    fun percentageCalc() {
        _syncStatus.update { true }
        var sumSteps = 0
        playersList.forEach { it ->
            sumSteps += it.steps
        }
        playersList.forEach { it ->
            it.percentage = it.steps.toFloat() / sumSteps
        }
        playersList.sortByDescending { it.percentage }
        _syncStatus.update { false }
    }

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
    }

    suspend fun syncData(login: String, steps: Int): String? {
        _syncStatus.update { true }
        Log.d("steps_debug", "StatsRepo syncData called with steps: $steps")
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
        percentageMax()
        _syncStatus.update { false }
        return data.errorMessage
    }
}