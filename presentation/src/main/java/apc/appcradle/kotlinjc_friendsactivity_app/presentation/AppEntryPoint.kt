package apc.appcradle.kotlinjc_friendsactivity_app.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.domain.models.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.SettingsViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.CompactText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.ExpandedText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.MediumText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.MyTypography
import org.koin.androidx.compose.koinViewModel

val LocalAppTypography = compositionLocalOf<MyTypography> { error("no typography provided") }

@Composable
fun AppEntryPoint(windowSizeClass: WindowSizeClass) {

    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.settingsState.collectAsStateWithLifecycle()

    val appTypography = when (settingsState.savedScale) {
        0.5f -> CompactText
        1.0f -> MediumText
        1.5f -> ExpandedText
        else -> MediumText
    }

    val theme = when (settingsState.savedTheme) {
        AppThemes.Dark -> true
        AppThemes.Light -> false
        AppThemes.System -> isSystemInDarkTheme()
    }

    KotlinJC_FriendsActivity_appTheme(
        darkTheme = theme,
        windowSizeClass = windowSizeClass
    ) {
        CompositionLocalProvider(
            LocalAppTypography provides appTypography
        ) {
            NavigationHost()
        }
    }
}