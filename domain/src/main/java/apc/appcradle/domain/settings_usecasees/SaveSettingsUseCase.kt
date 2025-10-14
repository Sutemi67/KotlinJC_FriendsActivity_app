package apc.appcradle.domain.settings_usecasees

import apc.appcradle.domain.SettingsRepository
import apc.appcradle.domain.models.local_data.SharedPreferencesData

class SaveSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(saveData: SharedPreferencesData) =
        settingsRepository.saveSettingsData(saveData)
}