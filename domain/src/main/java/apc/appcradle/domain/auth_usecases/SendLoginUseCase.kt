package apc.appcradle.domain.auth_usecases

import apc.appcradle.domain.NetworkClient
import apc.appcradle.domain.TokenRepository

class SendLoginUseCase(
    private val networkClient: NetworkClient
) {
    operator fun invoke() :String?= networkClient.getLogin()
}