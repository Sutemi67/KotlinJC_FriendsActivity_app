package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
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

fun isTodayMonday(): Boolean {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
}

//fun Activity.installSplashScreen() {
//    val splashScreen = SplashScreen(this)
//    splashScreen.install()
//    return splashScreen
//}

@Composable
fun nonScaledSp(size: Int): TextUnit {
    val fontScale = LocalDensity.current.fontScale
    return (size / fontScale).sp
}

fun openDonate(context: Context) {
    val url = "https://pay.cloudtips.ru/p/2d71d3e5"
//    val url = "https://yoomoney.ru/to/410017351998063"
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}
