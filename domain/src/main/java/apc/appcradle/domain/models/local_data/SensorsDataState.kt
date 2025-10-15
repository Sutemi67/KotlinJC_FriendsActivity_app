package apc.appcradle.domain.models.local_data

data class SensorsDataState(

    val isSensorsAvailable: Boolean = false,

    //UserData
    val userWeeklySteps: Int = 0,
    val userAllSteps: Int = 0,
)
