package apc.appcradle.kotlinjc_friendsactivity_app.data.configs

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.domain.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class TokenRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    private val _loginFlow = MutableStateFlow<String?>(null)
    val loginFlow: StateFlow<String?> = _loginFlow.asStateFlow()
    private fun updateLogin(login: String?) = _loginFlow.update { login }

    override suspend fun saveToken(login: String, token: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit {
            putString(AUTH_ID, token)
            putString(LOGIN_ID, login)
        }
        updateLogin(login)
    }

    override suspend fun saveNewLogin(newLogin: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit { putString(LOGIN_ID, newLogin) }
        updateLogin(newLogin)
    }

    override suspend fun saveOfflineToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit { putString(AUTH_ID, "offline") }
        updateLogin(null)
    }

    override suspend fun getLogin(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(LOGIN_ID, null)
    }

    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(AUTH_ID, null)
    }

    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit { remove(AUTH_ID) }
        updateLogin(null)
    }

    companion object {
        const val AUTH_ID = "auth_token"
        const val LOGIN_ID = "login"
    }
}