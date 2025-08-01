package apc.appcradle.kotlinjc_friendsactivity_app

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Preview(uiMode = Configuration.UI_MODE_TYPE_NORMAL)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
annotation class ThemePreviewsNoUi

@Preview(uiMode = Configuration.UI_MODE_TYPE_NORMAL, showSystemUi = true, device = "id:pixel_5")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true,
    device = "id:pixel_5"
)
annotation class ThemePreviews

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false,
    device = "spec:width=1080px,height=2220px,dpi=160", name = "160dpi"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false,
    device = "spec:width=1080px,height=2220px,dpi=240", name = "240dpi"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false,
    device = "spec:width=1080px,height=2220px,dpi=320", name = "320dpi"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false,
    device = "spec:width=1080px,height=2220px,dpi=480", name = "480dpi"
)
annotation class PreviewsDifferentSizes

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningServices = manager.getRunningServices(Integer.MAX_VALUE)

    return runningServices.any { it.service.className == serviceClass.name }
}

fun isTodayMonday(): Boolean {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
}

@Composable
fun nonScaledSp(size: Int): TextUnit {
    val fontScale = LocalDensity.current.fontScale
    return (size / fontScale).sp
}
