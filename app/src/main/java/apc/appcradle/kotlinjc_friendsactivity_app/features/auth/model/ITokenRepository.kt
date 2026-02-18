package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model

import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.TokenState
import kotlinx.coroutines.flow.StateFlow

interface ITokenRepository {
    suspend fun saveToken(login: String, token: String)
    suspend fun saveOfflineToken()
    suspend fun getSavedLogin()
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun saveNewLogin(newLogin: String)
    val tokenFlow: StateFlow<TokenState>
}