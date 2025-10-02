package apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.in_app_states

data class RatingsData(
    val login: String,
    val steps: Int,
    val weeklySteps: Int,
    var percentage: Float
)