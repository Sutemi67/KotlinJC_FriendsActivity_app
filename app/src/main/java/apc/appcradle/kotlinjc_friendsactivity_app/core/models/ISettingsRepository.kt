package apc.appcradle.kotlinjc_friendsactivity_app.core.models

import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsState
import kotlinx.coroutines.flow.StateFlow

interface ISettingsRepository {
    val settingsState: StateFlow<SettingsState>
    fun saveSettingsData(state: SettingsState)
    fun loadSettingsData(): SettingsState
}