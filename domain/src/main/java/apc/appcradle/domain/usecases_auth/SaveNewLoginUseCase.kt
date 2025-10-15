package apc.appcradle.domain.usecases_auth

import apc.appcradle.domain.TokenRepository

class SaveNewLoginUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(newLogin: String) = tokenRepository.saveNewLogin(newLogin)
}