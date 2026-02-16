package apc.appcradle.kotlinjc_friendsactivity_app.features.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.ITokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class UiState {
    OFFLINE, LOGGED_IT, LOGGED_OUT, SPLASH
}

data class TokenState(
    val login: String? = null,
    val token: String? = null,
    val uiState: UiState = UiState.SPLASH
)

class TokenRepository(
    private val sharedPreferences: SharedPreferences
) : ITokenRepository {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _tokenFlow = MutableStateFlow(TokenState())
    override val tokenFlow: StateFlow<TokenState> = _tokenFlow.asStateFlow()

    init {
        scope.launch {
            getToken()
            getSavedLogin()
            setUiState()
        }
    }

    private fun setUiState() {
        scope.launch {
            tokenFlow.collect { state ->
                logger(LoggerType.Debug, "token state changed -> ${tokenFlow.value}")
                when {
                    state.login != null && state.token != null -> {
                        _tokenFlow.update { it.copy(uiState = UiState.LOGGED_IT) }
                    }

                    state.login == null && state.token != null -> {
                        _tokenFlow.update { it.copy(uiState = UiState.OFFLINE) }
                    }

                    state.login == null && state.token == null -> {
                        _tokenFlow.update { it.copy(uiState = UiState.LOGGED_OUT) }
                    }
                }
            }
        }
    }

    override suspend fun saveToken(login: String, token: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit {
            putString(AUTH_ID, token)
            putString(LOGIN_ID, login)
        }
        _tokenFlow.update { it.copy(login = login, token = token) }
    }

    override suspend fun saveNewLogin(newLogin: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit { putString(LOGIN_ID, newLogin) }
        _tokenFlow.update { it.copy(login = newLogin) }
    }

    override suspend fun saveOfflineToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit { putString(AUTH_ID, "offline") }
        _tokenFlow.update { it.copy(login = null, token = "offline") }
    }

    override suspend fun getSavedLogin(): String? = withContext(Dispatchers.IO) {
        val login = sharedPreferences.getString(LOGIN_ID, null)
        _tokenFlow.update { it.copy(login = login) }
        login
    }

    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        val token = sharedPreferences.getString(AUTH_ID, null)
        _tokenFlow.update { it.copy(token = token) }
        token
    }

    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit { remove(AUTH_ID) }
        _tokenFlow.update { it.copy(login = null, token = null) }
    }

    companion object {
        const val AUTH_ID = "auth_token"
        const val LOGIN_ID = "login"
    }
}