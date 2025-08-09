package apc.appcradle.kotlinjc_friendsactivity_app.data

import android.content.SharedPreferences
import androidx.core.content.edit
import apc.appcradle.kotlinjc_friendsactivity_app.domain.StepsStorage

class StepsStorageImpl(
    private val sharedPreferences: SharedPreferences
) : StepsStorage {
    companion object {
        const val STEPS_DAILY_ID = "steps_daily_id"
        const val STEPS_WEEKLY_ID = "steps_weekly_id"
    }

    override fun saveTodaySteps(todaySteps: Int) {
        sharedPreferences.edit { putInt(STEPS_DAILY_ID, todaySteps) }
    }

    override fun getWeeklySteps(): Int {
        TODO("Not yet implemented")
    }
}