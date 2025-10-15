package apc.appcradle.domain.usecases_auth

import apc.appcradle.domain.TokenRepository

class GetTokenUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke() :String?= tokenRepository.getLogin()
}