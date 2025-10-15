package apc.appcradle.domain.usecases_auth

import apc.appcradle.domain.TokenRepository

class LogoutUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke() = tokenRepository.clearToken()
}
