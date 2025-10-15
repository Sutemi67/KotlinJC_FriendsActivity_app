package apc.appcradle.domain.usecases_auth

import apc.appcradle.domain.TokenRepository

class CheckPermanentAuthUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke() :String?= tokenRepository.getToken()
}