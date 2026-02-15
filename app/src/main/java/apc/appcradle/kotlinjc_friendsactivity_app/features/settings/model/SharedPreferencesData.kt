package apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model

import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppThemes

data class SharedPreferencesData(
    val savedTheme: AppThemes = AppThemes.System,
    val savedUserStep: Double = 0.65,
    val savedScale: Float = 1.0f,
    val savedIsServiceEnabled: Boolean = false
)