package apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model

import kotlinx.coroutines.flow.StateFlow

interface ISettingsRepository {
    val settingsState: StateFlow<SettingsState>
    fun saveSettingsData(state: SettingsState)
    fun loadSettingsData(): SettingsState
}