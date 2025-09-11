package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayersListSyncData
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.Steps
import apc.appcradle.kotlinjc_friendsactivity_app.whenNextMonday
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
        var maxSteps = 0
        playersList.forEach { it ->
            if (it.weeklySteps > maxSteps) maxSteps = it.weeklySteps
        }
        playersList.forEach { it ->
            it.percentage = it.weeklySteps.toFloat() / maxSteps
        }
        playersList.sortByDescending { it.percentage }
        _syncStatus.update { false }
        Log.d("dataTransfer", "StatsRepo sorted list $")
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
            data.friendsList.forEach { it ->
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
        return stepsSum * 0.4 / 1000
    }

    private fun calcLeaderDiff(login: String?): Double {
        var diff = 0.0
        if (playersList.isNotEmpty()) {
            val leader = playersList.first()
            val player = playersList.first { it.login == login }
            diff = (leader.steps - player.steps) * 0.4 / 1000
        }
        return diff
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
//            workManager.enqueue(trancateStepsRequest(10000))
//            workManager.enqueue(trancateStepsRequest(whenNextDayModern()))
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