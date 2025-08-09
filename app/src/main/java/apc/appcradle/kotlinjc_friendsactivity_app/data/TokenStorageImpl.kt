package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.domain.TokenStorage

class TokenStorageImpl(
    private val sharedPreferences: SharedPreferences
) : TokenStorage {
    override fun saveToken(login: String, token: String) {
        sharedPreferences.edit {
            putString("auth_token", token)
            putString("login", login)
        }
    }

    override fun getLogin(): String? {
        return sharedPreferences.getString("login", null)
    }

    override fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    override fun clearToken() {
        sharedPreferences.edit { remove("auth_token") }
    }
}