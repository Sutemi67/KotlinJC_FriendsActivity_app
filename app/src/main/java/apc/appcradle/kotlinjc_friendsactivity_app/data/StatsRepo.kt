package apc.appcradle.kotlinjc_friendsactivity_app.data

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.PlayerActivityData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatsRepo {
    private val _syncStatus = MutableStateFlow(false)
    val syncStatus: StateFlow<Boolean> = _syncStatus.asStateFlow()

    val playersList = mutableListOf(
        PlayerActivityData("Alexander", 4324, 0f),
        PlayerActivityData("Maria", 343, 0f),
        PlayerActivityData("Nicolas", 43242, 0f),
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
}