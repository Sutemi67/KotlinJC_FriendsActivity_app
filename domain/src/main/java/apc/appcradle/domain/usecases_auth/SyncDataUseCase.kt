package apc.appcradle.domain.usecases_auth

import apc.appcradle.domain.StatsRepository
import apc.appcradle.domain.models.network.PlayersListSyncData

class SyncDataUseCase(
    private val statsRepository: StatsRepository
) {
    suspend operator fun invoke(login: String, steps: Int, weeklySteps: Int): PlayersListSyncData =
        statsRepository.syncData(login, steps, weeklySteps)
}