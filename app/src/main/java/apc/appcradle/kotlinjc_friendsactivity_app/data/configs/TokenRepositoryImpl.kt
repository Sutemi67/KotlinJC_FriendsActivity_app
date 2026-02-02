package apc.appcradle.kotlinjc_friendsactivity_app.data.configs

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.domain.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TokenRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    private val _loginFlow = MutableStateFlow<String?>(null)
    val loginFlow: StateFlow<String?> = _loginFlow.asStateFlow()

    private fun updateLogin(login: String?) {
        _loginFlow.update { login }
    }

    override fun saveToken(login: String, token: String) {
        sharedPreferences.edit {
            putString(AUTH_ID, token)
            putString(LOGIN_ID, login)
        }
        updateLogin(login)
    }

    override fun saveNewLogin(newLogin: String) {
        sharedPreferences.edit {
            putString(LOGIN_ID, newLogin)
        }
        updateLogin(newLogin)
    }

    override fun saveOfflineToken() {
        sharedPreferences.edit { putString(AUTH_ID, "offline") }
        updateLogin(null)
    }

    override fun getLogin(): String? {
        val login = sharedPreferences.getString(LOGIN_ID, null)
        if (login != _loginFlow.value)
            updateLogin(login)
        return login
    }

    override fun getToken(): String? {
        return sharedPreferences.getString(AUTH_ID, null)
    }

    override fun clearToken() {
        sharedPreferences.edit { remove(AUTH_ID) }
        updateLogin(null)
    }

    companion object {
        const val AUTH_ID = "auth_token"
        const val LOGIN_ID = "login"
    }
}