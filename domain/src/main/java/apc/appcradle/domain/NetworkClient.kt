package apc.appcradle.domain

import apc.appcradle.domain.models.network.DataTransferState
import apc.appcradle.domain.models.network.responses.UserActivityResponse

interface NetworkClient {
    fun saveToken(login: String, token: String)
    suspend fun sendRegistrationInfo(login: String, password: String): DataTransferState
    suspend fun sendLoginInfo(login: String, password: String): DataTransferState
    suspend fun postUserDataAndSyncFriendsData(
        login: String,
        steps: Int,
        weeklySteps: Int
    ): UserActivityResponse
    suspend fun changeUserLogin(login: String, newLogin: String): Boolean
}