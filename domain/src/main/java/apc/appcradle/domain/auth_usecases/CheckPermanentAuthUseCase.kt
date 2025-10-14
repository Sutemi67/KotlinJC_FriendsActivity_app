package apc.appcradle.domain.auth_usecases

import apc.appcradle.domain.TokenRepository

class CheckPermanentAuthUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke() :String?= tokenRepository.getToken()
}