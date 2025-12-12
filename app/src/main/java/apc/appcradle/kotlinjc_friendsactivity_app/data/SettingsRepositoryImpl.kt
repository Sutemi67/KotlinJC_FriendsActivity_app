package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.data.SharedPreferencesData
import com.google.gson.Gson

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    companion object {
        const val SETTINGS_PREFS_ID = "settings_id"
    }

    private val gson = Gson()

    override fun saveSettingsData(currentSettingsData: SharedPreferencesData) {
        val json = gson.toJson(currentSettingsData)
        sharedPreferences.edit { putString(SETTINGS_PREFS_ID, json) }
        Log.i("theme", "saved:\n$json")
    }

    override fun loadSettingsData(): SharedPreferencesData {
        val json: String? = sharedPreferences.getString(SETTINGS_PREFS_ID, null)
        return if (json != null) {
            Log.i("theme", "loaded:\n$json")
            gson.fromJson(json, SharedPreferencesData::class.java)
        }
        else
            SharedPreferencesData()
    }

}