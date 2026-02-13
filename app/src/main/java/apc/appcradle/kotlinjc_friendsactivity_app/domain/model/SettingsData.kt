package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

import androidx.compose.runtime.Stable
import apc.appcradle.kotlinjc_friendsactivity_app.utils.USER_STEP_DEFAULT

@Stable
data class SettingsData(
    val currentTheme: AppThemes= AppThemes.System,
    val userScale: Float = 1.0f,
    val userStepLength: Double = USER_STEP_DEFAULT
)
