package apc.appcradle.domain.auth_usecases

import apc.appcradle.domain.TokenRepository

class OfflineUseUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke() = tokenRepository.saveOfflineToken()
}