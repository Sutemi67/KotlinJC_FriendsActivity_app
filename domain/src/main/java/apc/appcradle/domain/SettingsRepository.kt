package apc.appcradle.domain

import apc.appcradle.domain.models.local_data.SharedPreferencesData

interface SettingsRepository {
    fun saveSettingsData(currentSettingsData: SharedPreferencesData)
    fun loadSettingsData(): SharedPreferencesData
}