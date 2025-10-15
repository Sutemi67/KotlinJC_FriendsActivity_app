package apc.appcradle.domain.usecases_auth

import apc.appcradle.domain.TokenRepository

class OfflineUseUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke() = tokenRepository.saveOfflineToken()
}