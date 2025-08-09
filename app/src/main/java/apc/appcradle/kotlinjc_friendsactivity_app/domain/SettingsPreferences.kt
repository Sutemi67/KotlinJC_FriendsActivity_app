package apc.appcradle.kotlinjc_friendsactivity_app.domain

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppSavedSettingsData

interface SettingsPreferences {
    fun saveSteps(steps: Int)
    fun getSteps(): Int
    fun saveSettingsData(currentSettingsData: AppSavedSettingsData)
    fun loadSettingsData(): AppSavedSettingsData
}