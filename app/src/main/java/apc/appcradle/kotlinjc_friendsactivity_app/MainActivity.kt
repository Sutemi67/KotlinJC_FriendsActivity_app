package apc.appcradle.kotlinjc_friendsactivity_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.NavigationHost
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.CompactText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.ExpandedText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.MediumText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.MyTypography
import apc.appcradle.kotlinjc_friendsactivity_app.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.utils.logger
import org.koin.compose.viewmodel.koinViewModel

val LocalAppTypography = compositionLocalOf<MyTypography> { error("no typography provided") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = koinViewModel()
            val settings = viewModel.settingsState.collectAsState().value

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
                    logger(LoggerType.Error, "navhost recomposed in activity")
                    NavigationHost()
                }
            }
        }
    }
}