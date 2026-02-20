package apc.appcradle.kotlinjc_friendsactivity_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.CompactText
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.ExpandedText
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.MediumText
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.MyTypography
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.MainActivityApp
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.ISettingsRepository
import org.koin.compose.koinInject

val LocalAppTypography = compositionLocalOf<MyTypography> { error("no typography provided") }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FriendsActivityApp()
        }
    }
}

@Composable
private fun FriendsActivityApp() {
    val settingsRepository: ISettingsRepository = koinInject()
    val settings = settingsRepository.settingsState.collectAsStateWithLifecycle().value

    val appTypography = when (settings.userScale) {
        0.5f -> CompactText
        1.0f -> MediumText
        1.5f -> ExpandedText
        else -> MediumText
    }

    val theme = when (settings.currentTheme) {
        AppThemes.Dark -> true
        AppThemes.Light -> false
        AppThemes.System -> isSystemInDarkTheme()
    }

    KotlinJC_FriendsActivity_appTheme(
        darkTheme = theme,
    ) {
        CompositionLocalProvider(
            LocalAppTypography provides appTypography
        ) {
            logger(LoggerType.Recomposition, "FriendsActivityApp", "navhost recomposed in activity")
            MainActivityApp()
        }
    }
}