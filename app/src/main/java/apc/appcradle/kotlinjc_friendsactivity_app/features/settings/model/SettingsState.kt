package apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model

import androidx.compose.runtime.Stable
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseState
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.USER_STEP_DEFAULT

@Stable
data class SettingsState(
    val userLogin: String? = null,
    val currentTheme: AppThemes = AppThemes.System,
    val userScale: Float = 1.0f,
    val userStepLength: Double = USER_STEP_DEFAULT,
    val serviceSavedOption: Boolean = false,
    val isServiceRunning: Boolean = false
) : BaseState