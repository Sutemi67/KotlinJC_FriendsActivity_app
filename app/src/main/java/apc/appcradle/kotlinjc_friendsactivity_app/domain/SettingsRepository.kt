package apc.appcradle.kotlinjc_friendsactivity_app.domain

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.SharedPreferencesData

interface SettingsRepository {
    fun saveSettingsData(currentSettingsData: SharedPreferencesData)
    fun loadSettingsData(): SharedPreferencesData
}