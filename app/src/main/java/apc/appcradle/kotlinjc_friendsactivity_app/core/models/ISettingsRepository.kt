package apc.appcradle.kotlinjc_friendsactivity_app.core.models

import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SharedPreferencesData

interface ISettingsRepository {
    fun saveSettingsData(currentSettingsData: SharedPreferencesData)
    fun loadSettingsData(): SharedPreferencesData
}