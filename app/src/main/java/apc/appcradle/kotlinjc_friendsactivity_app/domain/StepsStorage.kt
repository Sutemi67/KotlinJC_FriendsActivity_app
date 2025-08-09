package apc.appcradle.kotlinjc_friendsactivity_app.domain

interface StepsStorage {
    fun saveTodaySteps(todaySteps: Int)
    fun getWeeklySteps(): Int
}