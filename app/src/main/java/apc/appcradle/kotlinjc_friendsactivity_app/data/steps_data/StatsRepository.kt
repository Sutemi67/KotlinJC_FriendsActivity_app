package apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.Steps
import apc.appcradle.kotlinjc_friendsactivity_app.services.workers.trancateStepsRequest
import apc.appcradle.kotlinjc_friendsactivity_app.utils.USER_STEP_DEFAULT
import apc.appcradle.kotlinjc_friendsactivity_app.utils.whenNextMonday
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatsRepository(
    private val networkClient: NetworkClient,
    private val workManager: WorkManager,
    private val sharedPreferences: SharedPreferences
) {
    private val _syncStatus = MutableStateFlow(false)
    val syncStatus: StateFlow<Boolean> = _syncStatus.asStateFlow()

    private var playersList = mutableListOf<PlayerActivityData>()
    private var isFirstAppStart = true

    private fun percentageMax() {
        _syncStatus.update { true }
        val maxSteps = playersList.maxOfOrNull { it.weeklySteps } ?: 0
        playersList.forEach { player ->
            player.percentage = if (maxSteps > 0) {
                player.weeklySteps.toFloat() / maxSteps
            } else 0f
        }
        playersList.sortByDescending { it.weeklySteps }
        _syncStatus.update { false }
        Log.d("dataTransfer", "StatsRepo sorted list")
    }

    suspend fun syncData(login: String, steps: Int, weeklySteps: Int): PlayersListSyncData {
        _syncStatus.update { true }
        try {
            Log.d("dataTransfer", "StatsRepo syncData called with steps: $steps, $weeklySteps")
            val data = networkClient.postUserDataAndSyncFriendsData(
                login = login,
                steps = steps,
                weeklySteps = weeklySteps
            )
            val newPlayersList = mutableListOf<PlayerActivityData>()
            data.friendsList.forEach {
                newPlayersList.add(
                    PlayerActivityData(
                        login = it.login,
                        steps = it.steps,
                        weeklySteps = it.weeklySteps,
                        percentage = 0f
                    )
                )
            }
            playersList = newPlayersList
            percentageMax()
            val sumKm = calcSumKm()
            val difference = calcLeaderDiff(login)
            _syncStatus.update { false }
            return PlayersListSyncData(
                playersList = playersList,
                summaryKm = sumKm,
                leaderDifferenceKm = difference,
                errorMessage = data.errorMessage,
                leader = data.leader
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
                stepsSum += player.weeklySteps
            }
        }
        Log.e("dataTransfer", "sum of weekly steps is $stepsSum")
        return stepsSum * USER_STEP_DEFAULT / 1000
    }

    private fun calcLeaderDiff(login: String?): Double {
        if (playersList.isEmpty() || login.isNullOrBlank()) return 0.0
        val leader = playersList.maxByOrNull { it.weeklySteps } ?: return 0.0
        val player = playersList.firstOrNull { it.login == login } ?: return 0.0
        val diffKm = (leader.weeklySteps - player.weeklySteps) * USER_STEP_DEFAULT / 1000
        Log.e("difference", "$leader\n$player\n$diffKm")
        return diffKm
    }

    init {
        isFirstStartCheck()
        planningTrancateSteps()
    }

    private fun isFirstStartCheck() {
        isFirstAppStart = sharedPreferences.getBoolean(FIRST_START_ID, true)
        Log.d("worker", "statRepo,isFirstStartCheck -> $isFirstAppStart")
    }

    fun saveAllSteps(steps: Steps) {
        sharedPreferences.edit {
            putInt(STEPS_ID, steps.allSteps)
            putInt(STEPS_WEEKLY_ID, steps.weeklySteps)
        }
    }

    fun loadSteps(): Steps {
        val steps = Steps(
            allSteps = sharedPreferences.getInt(STEPS_ID, 0),
            weeklySteps = sharedPreferences.getInt(STEPS_WEEKLY_ID, 0)
        )
        Log.d("worker", "statRepo,getSteps -> $steps")
        return steps
    }

    fun planningTrancateSteps() {
        if (isFirstAppStart) {
            isFirstAppStart = false
            sharedPreferences.edit { putBoolean(FIRST_START_ID, false) }
            workManager.enqueue(trancateStepsRequest(whenNextMonday()))
            Log.d("worker", "statRepo,planningTrancateSteps -> ${whenNextMonday()}")
        }
    }

    fun trancate() {
        isFirstAppStart = true
        sharedPreferences.edit {
            putInt(STEPS_WEEKLY_ID, 0)
            putBoolean(FIRST_START_ID, true)
        }
    }

    companion object {
        const val STEPS_ID = "steps_id"
        const val STEPS_WEEKLY_ID = "steps_weekly_id"
        const val FIRST_START_ID = "is_first_start"
    }
}