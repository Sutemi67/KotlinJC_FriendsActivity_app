package apc.appcradle.kotlinjc_friendsactivity_app.domain

interface TokenRepository {
    suspend fun saveToken(login: String, token: String)
    suspend fun saveOfflineToken()
    suspend fun getLogin(): String?
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun saveNewLogin(newLogin: String)
}