package apc.appcradle.kotlinjc_friendsactivity_app.core.models

interface ITokenRepository {
    suspend fun saveToken(login: String, token: String)
    suspend fun saveOfflineToken()
    suspend fun getSavedLogin(): String?
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun saveNewLogin(newLogin: String)
}