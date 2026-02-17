package apc.appcradle.kotlinjc_friendsactivity_app.features.settings.data

import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.ISettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.ITokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
class SettingsRepository(
    private val sharedPreferences: SharedPreferences,
    private val tokenRepository: ITokenRepository
) : ISettingsRepository {
    private val gson = Gson()
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val mutableState: MutableStateFlow<SettingsState> =
        MutableStateFlow(loadSettingsData())
    override val settingsState = mutableState.asStateFlow()

    init {
        scope.launch {
            tokenRepository.tokenFlow.collect { token ->
                mutableState.update { it.copy(userLogin = token.login) }
            }
        }
    }

    override fun saveSettingsData(state: SettingsState) {
        val json = gson.toJson(state)
        sharedPreferences.edit { putString(SETTINGS_PREFS_ID, json) }
        mutableState.update { state }
    }

    override fun loadSettingsData(): SettingsState {
        val json: String? = sharedPreferences.getString(SETTINGS_PREFS_ID, null)

        return if (json != null) {
            gson.fromJson(json, SettingsState::class.java)
        } else SettingsState()
    }

    companion object {
        const val SETTINGS_PREFS_ID = "settings_id"
    }
}