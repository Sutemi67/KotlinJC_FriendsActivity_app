package apc.appcradle.kotlinjc_friendsactivity_app.domain

interface TokenRepository {
    fun saveToken(login: String, token: String)
    fun saveOfflineToken()
    fun getLogin(): String?

    fun getToken(): String?

    fun clearToken()
    fun saveNewLogin(newLogin: String)
}