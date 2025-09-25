package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

data class SharedPreferencesData(
    val savedTheme: AppThemes = AppThemes.System,
    val savedUserStep: Double = 0.65,
    val savedScale: Float = 1.0f
)
