package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsPreferences
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppSavedSettingsData
import com.google.gson.Gson

class SettingsPreferencesImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsPreferences {

    companion object {
        const val STEPS_PREFS_ID = "steps_id"
        const val SETTINGS_PREFS_ID = "settings_id"
    }

    val gson = Gson()

    override fun saveSteps(steps: Int) {
        sharedPreferences.edit { putInt(STEPS_PREFS_ID, steps) }
    }

    override fun getSteps(): Int {
        return sharedPreferences.getInt(STEPS_PREFS_ID, 0)
    }

    override fun saveSettingsData(currentSettingsData: AppSavedSettingsData) {
        val json = gson.toJson(currentSettingsData)
        sharedPreferences.edit { putString(SETTINGS_PREFS_ID, json) }
    }

    override fun loadSettingsData(): AppSavedSettingsData {
        val json: String? = sharedPreferences.getString(SETTINGS_PREFS_ID, null)
        return if (json != null)
            gson.fromJson(json, AppSavedSettingsData::class.java)
        else
            AppSavedSettingsData()
    }

}