package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(uiMode = Configuration.UI_MODE_TYPE_NORMAL, showSystemUi = false, showBackground = false)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = false, showBackground = false)
annotation class ThemePreviewsNoUi

@Preview(uiMode = Configuration.UI_MODE_TYPE_NORMAL, showSystemUi = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
annotation class ThemePreviews