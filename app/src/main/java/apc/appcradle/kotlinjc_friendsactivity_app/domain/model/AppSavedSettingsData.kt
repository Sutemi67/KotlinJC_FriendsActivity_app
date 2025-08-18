package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

data class AppSavedSettingsData(
    val savedTheme: AppThemes = AppThemes.System,
    val savedUserStep: Double = 0.4,
    val savedScale: Float = 1.0f
)
