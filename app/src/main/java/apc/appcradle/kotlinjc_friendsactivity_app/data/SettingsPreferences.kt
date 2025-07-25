package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsPreferences(
    private val sharedPreferences: SharedPreferences
) {
    fun saveSteps(steps: Int) {
        sharedPreferences.edit { putInt("steps", steps) }
    }

    fun getSteps(): Int {
        return sharedPreferences.getInt("steps", 0)
    }
}