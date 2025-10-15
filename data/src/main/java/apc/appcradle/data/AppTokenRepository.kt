package apc.appcradle.data

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.domain.TokenRepository

class AppTokenRepository(
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    companion object {
        const val TOKEN_ID = "auth_token"
        const val LOGIN_ID = "login"
    }

    override fun saveToken(login: String, token: String) {
        sharedPreferences.edit {
            putString(TOKEN_ID, token)
            putString(LOGIN_ID, login)
        }
    }

    override fun saveNewLogin(newLogin: String) {
        sharedPreferences.edit {
            putString(LOGIN_ID, newLogin)
        }
    }

    override fun saveOfflineToken() {
        sharedPreferences.edit { putString(TOKEN_ID, "offline") }
    }

    override fun getLogin(): String? {
        return sharedPreferences.getString(LOGIN_ID, null)
    }

    override fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_ID, null)
    }

    override fun clearToken() {
        sharedPreferences.edit { remove(TOKEN_ID) }
    }
}