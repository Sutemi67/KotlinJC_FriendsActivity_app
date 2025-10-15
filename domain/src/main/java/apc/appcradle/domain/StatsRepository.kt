package apc.appcradle.domain

import apc.appcradle.domain.models.network.PlayersListSyncData
import apc.appcradle.domain.models.network.Steps

interface StatsRepository {
    suspend fun syncData(login: String, steps: Int, weeklySteps: Int): PlayersListSyncData
    fun saveAllSteps(steps: Steps)
    fun loadSteps(): Steps
    fun planningTrancateSteps()
    fun trancate()
}