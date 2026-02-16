package apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model

import android.content.Context
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseEvents

sealed class SettingsEvents : BaseEvents {
    data class ChangeScale(val newScaleValue: Float) : SettingsEvents()
    data class ChangeLogin(val newLogin: String) : SettingsEvents()
    data class ChangeStepLength(val newStepValue: Double) : SettingsEvents()
    data class ChangeTheme(val newTheme: AppThemes) : SettingsEvents()
    data class StartService(val context: Context) : SettingsEvents()
    data class StopService(val context: Context) : SettingsEvents()
    data class OnServiceCheckerClick(val checkerValue: Boolean, val context: Context) :
        SettingsEvents()

    object SaveSettings : SettingsEvents()
    object LoadSettings : SettingsEvents()
    object Logout : SettingsEvents()
}