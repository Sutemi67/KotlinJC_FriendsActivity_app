package apc.appcradle.kotlinjc_friendsactivity_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import apc.appcradle.kotlinjc_friendsactivity_app.LocalAppTypography

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun KotlinJC_FriendsActivity_appTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    windowSizeClass: WindowSizeClass? = null,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

//    val currentWindowSizeClass = windowSizeClass ?: WindowSizeClass.calculateFromSize(
//        DpSize(
//            LocalConfiguration.current.screenWidthDp.dp,
//            LocalConfiguration.current.screenHeightDp.dp
//        )
//    )

//    val density = LocalDensity.current.density
//    Log.d("density", "$density")
//    val typography: Typography = when (currentWindowSizeClass.widthSizeClass) {
//        WindowWidthSizeClass.Expanded -> ExpandedTypography
//        WindowWidthSizeClass.Medium -> MediumTypography
//        else -> when {
//            density >= 3.1f -> CompactHighDpiTypography
//            density >= 2.5f -> CompactTypography
//            else -> CompactTypography
//        }
//    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }
    CompositionLocalProvider(
        LocalAppTypography provides MediumText
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
        ) {
            Surface(
                tonalElevation = 5.dp
            ) {
                content()
            }
        }
    }
}
