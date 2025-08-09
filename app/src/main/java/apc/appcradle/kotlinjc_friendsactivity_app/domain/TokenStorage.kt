package apc.appcradle.kotlinjc_friendsactivity_app.domain

interface TokenStorage {
    fun saveToken(login: String, token: String)

    fun getLogin(): String?

    fun getToken(): String?

    fun clearToken()
}