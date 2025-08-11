package apc.appcradle.kotlinjc_friendsactivity_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.NavigationHost
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.CompactTypography
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.ExpandedTypography
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.MediumTypography
import org.koin.androidx.compose.koinViewModel

val LocalTypography = compositionLocalOf<Typography> { error("no typography provided") }

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = koinViewModel<MainViewModel>()
            val windowSizeClass = calculateWindowSizeClass(this)
            val state by viewModel.state.collectAsStateWithLifecycle()

            val appTypography = when (state.userScale) {
                0.5f -> CompactTypography
                1.0f -> MediumTypography
                1.5f -> ExpandedTypography
                else -> MediumTypography
            }

            val theme = when (state.currentTheme) {
                AppThemes.Dark -> true
                AppThemes.Light -> false
                AppThemes.System -> isSystemInDarkTheme()
            }

            CompositionLocalProvider(
                LocalTypography provides appTypography
            ) {
                KotlinJC_FriendsActivity_appTheme(
                    darkTheme = theme,
                    windowSizeClass = windowSizeClass
                ) {
                    NavigationHost(viewModel, state)
                }
            }
        }
    }
}
