package apc.appcradle.kotlinjc_friendsactivity_app.features.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.ITokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

enum class UiState {
    OFFLINE, LOGGED_IN, LOGGED_OUT, SPLASH
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
    override val tokenFlow: StateFlow<TokenState> = _tokenFlow
        .map { data ->
            TokenState(
                login = data.login,
                token = data.token,
                uiState = calculateUiState(data.login, data.token)
            )
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TokenState()
        )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val token = sharedPreferences.getString(TOKEN_ID, null)
        val login = sharedPreferences.getString(LOGIN_ID, null)
        _tokenFlow.update { it.copy(login = login, token = token) }
    }

    private fun calculateUiState(login: String?, token: String?): UiState {
        return when {
            token == OFFLINE_TOKEN -> UiState.OFFLINE
            login != null && token != null -> UiState.LOGGED_IN
            login == null && token == null -> UiState.LOGGED_OUT
            else -> UiState.SPLASH
        }
    }

    override suspend fun saveToken(login: String, token: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit {
            putString(TOKEN_ID, token)
            putString(LOGIN_ID, login)
        }
        _tokenFlow.update { it.copy(login = login, token = token) }
    }

    override suspend fun saveNewLogin(newLogin: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit { putString(LOGIN_ID, newLogin) }
        _tokenFlow.update { it.copy(login = newLogin) }
    }

    override suspend fun saveOfflineToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit { putString(TOKEN_ID, OFFLINE_TOKEN) }
        sharedPreferences.edit { putString(LOGIN_ID, OFFLINE_USER_NICKNAME) }
        _tokenFlow.update { it.copy(login = OFFLINE_USER_NICKNAME, token = OFFLINE_TOKEN) }
    }

    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        val token = sharedPreferences.getString(TOKEN_ID, null)
        _tokenFlow.update { it.copy(token = token) }
        token
    }

    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit {
            remove(TOKEN_ID)
            remove(LOGIN_ID)
        }
        _tokenFlow.update { it.copy(login = null, token = null) }
    }

    companion object {
        const val TOKEN_ID = "auth_token"
        const val LOGIN_ID = "login"
        const val OFFLINE_TOKEN = "offline"
        const val OFFLINE_USER_NICKNAME = "__Offline_user_nickname__"
    }
}