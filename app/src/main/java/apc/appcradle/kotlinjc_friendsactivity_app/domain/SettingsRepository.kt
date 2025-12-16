package apc.appcradle.kotlinjc_friendsactivity_app.domain

import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.model.SharedPreferencesData

interface SettingsRepository {
    fun saveSettingsData(currentSettingsData: SharedPreferencesData)
    fun loadSettingsData(): SharedPreferencesData
}