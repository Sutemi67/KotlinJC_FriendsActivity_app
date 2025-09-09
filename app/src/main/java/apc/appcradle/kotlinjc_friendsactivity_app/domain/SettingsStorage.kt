package apc.appcradle.kotlinjc_friendsactivity_app.domain

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppSavedSettingsData

interface SettingsStorage {
    fun saveSettingsData(currentSettingsData: AppSavedSettingsData)
    fun loadSettingsData(): AppSavedSettingsData
}