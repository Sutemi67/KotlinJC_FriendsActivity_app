package apc.appcradle.kotlinjc_friendsactivity_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.NavigationHost
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import org.koin.androidx.compose.koinViewModel

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

            KotlinJC_FriendsActivity_appTheme(
                darkTheme = when (state.currentTheme) {
                    AppThemes.Dark -> true
                    AppThemes.Light -> false
                    AppThemes.System -> isSystemInDarkTheme()
                },
                windowSizeClass = windowSizeClass
            ) {
                LaunchedEffect(state) {
                    Log.e(
                        "theme",
                        "main activity theme changed to ${state.currentTheme}"
                    )
                }
                NavigationHost(viewModel)
            }
        }
    }
}
