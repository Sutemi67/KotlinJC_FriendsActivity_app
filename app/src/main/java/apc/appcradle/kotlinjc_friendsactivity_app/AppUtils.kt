package apc.appcradle.kotlinjc_friendsactivity_app

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(uiMode = Configuration.UI_MODE_TYPE_NORMAL)
@Preview(device = "spec:parent=pixel_5,orientation=landscape",
    uiMode = Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL
)
annotation class ThemePreviewsNoUi

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
annotation class ThemePreviews

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningServices = manager.getRunningServices(Integer.MAX_VALUE)

    return runningServices.any { it.service.className == serviceClass.name }
}