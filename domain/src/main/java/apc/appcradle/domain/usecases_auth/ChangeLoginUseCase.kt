package apc.appcradle.domain.usecases_auth

import apc.appcradle.domain.NetworkClient

class ChangeLoginUseCase(
    private val networkClient: NetworkClient
) {
    suspend operator fun invoke(login: String, newLogin: String): Boolean =
        networkClient.changeUserLogin(login, newLogin)
}