package apc.appcradle.domain.auth_usecases

import apc.appcradle.domain.TokenRepository

class LogoutUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke() = tokenRepository.clearToken()
}
