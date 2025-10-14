package apc.appcradle.domain.auth_usecases

import apc.appcradle.domain.TokenRepository

class GetLoginUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke() :String?= tokenRepository.getLogin()
}