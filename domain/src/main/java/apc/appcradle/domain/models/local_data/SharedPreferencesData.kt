package apc.appcradle.domain.models.local_data

import apc.appcradle.domain.models.AppThemes

data class SharedPreferencesData(
    val savedTheme: AppThemes = AppThemes.System,
    val savedUserStep: Double = 0.65,
    val savedScale: Float = 1.0f,
    val savedIsServiceEnabled: Boolean = false
)