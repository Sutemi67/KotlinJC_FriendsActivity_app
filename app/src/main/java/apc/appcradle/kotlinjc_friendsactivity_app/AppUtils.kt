package apc.appcradle.kotlinjc_friendsactivity_app

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar

@Preview(uiMode = Configuration.UI_MODE_TYPE_NORMAL)
@Preview(
    device = "spec:parent=pixel_5,orientation=landscape",
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

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningServices = manager.getRunningServices(Integer.MAX_VALUE)

    return runningServices.any { it.service.className == serviceClass.name }
}

fun isTodayMonday(): Boolean {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
}