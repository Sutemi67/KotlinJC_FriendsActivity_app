package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar

fun whenNextMonday(): Long {
    val now = Calendar.getInstance()
    val target = now.clone() as Calendar
    target.apply {
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(now)) add(Calendar.WEEK_OF_YEAR, 1)
    }
    return target.timeInMillis - now.timeInMillis
}

@RequiresApi(Build.VERSION_CODES.O)
fun whenNextDayModern(): Long {
    val now = ZonedDateTime.now()
    val nextDay = now.toLocalDate()
        .plusDays(1)
        .atStartOfDay(now.zone)
    return ChronoUnit.MILLIS.between(now, nextDay)
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
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}
