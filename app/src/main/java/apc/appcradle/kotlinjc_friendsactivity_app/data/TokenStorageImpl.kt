package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.domain.TokenStorage

class TokenStorageImpl(
    private val sharedPreferences: SharedPreferences
) : TokenStorage {
    companion object {
        const val AUTH_ID = "auth_token"
        const val LOGIN_ID = "login"
    }

    override fun saveToken(login: String, token: String) {
        sharedPreferences.edit {
            putString(AUTH_ID, token)
            putString(LOGIN_ID, login)
        }
    }

    override fun saveOfflineToken() {
        sharedPreferences.edit { putString(AUTH_ID, "offline") }
    }

    override fun getLogin(): String? {
        return sharedPreferences.getString(LOGIN_ID, null)
    }

    override fun getToken(): String? {
        return sharedPreferences.getString(AUTH_ID, null)
    }

    override fun clearToken() {
        sharedPreferences.edit { remove(AUTH_ID) }
    }
}