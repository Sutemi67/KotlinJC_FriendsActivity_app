package apc.appcradle.domain.usecases_auth

import apc.appcradle.domain.NetworkClient
import apc.appcradle.domain.models.network.DataTransferState

class SendLoginUseCase(
    private val networkClient: NetworkClient
) {
    suspend operator fun invoke(login: String, password: String): DataTransferState =
        networkClient.sendLoginInfo(login, password)
}