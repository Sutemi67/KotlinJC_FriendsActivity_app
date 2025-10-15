package apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models

import androidx.lifecycle.ViewModel
import apc.appcradle.domain.models.AppThemes
import apc.appcradle.domain.models.local_data.SharedPreferencesData
import apc.appcradle.domain.usecases_settings.LoadSettingsUseCase
import apc.appcradle.domain.usecases_settings.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SharedPreferencesData())
    val settingsState: StateFlow<SharedPreferencesData> = _settingsState.asStateFlow()

    init {
        loadSettings()
    }

    fun saveSettings() = saveSettingsUseCase(
        SharedPreferencesData(
            savedTheme = settingsState.value.savedTheme,
            savedScale = settingsState.value.savedScale,
            savedUserStep = settingsState.value.savedUserStep,
            savedIsServiceEnabled = settingsState.value.savedIsServiceEnabled
        )
    )

    fun loadSettings() {
        val settings = loadSettingsUseCase()
        _settingsState.update { settings }
    }

    fun changeTheme(currentTheme: AppThemes) {
        _settingsState.update { it.copy(savedTheme = currentTheme) }
        saveSettings()
    }

    fun changeScale(newScale: Float) {
        _settingsState.update { it.copy(savedScale = newScale) }
        saveSettings()
    }

    fun changeStepLength(newStepLength: Double) {
        _settingsState.update { it.copy(savedUserStep = newStepLength) }
        saveSettings()
    }
}