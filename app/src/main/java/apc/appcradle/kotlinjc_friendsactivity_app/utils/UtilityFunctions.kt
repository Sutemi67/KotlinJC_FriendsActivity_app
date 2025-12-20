package apc.appcradle.kotlinjc_friendsactivity_app.utils

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.util.Log
import androidx.core.net.toUri
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

//fun Activity.installSplashScreen() {
//    val splashScreen = SplashScreen(this)
//    splashScreen.install()
//    return splashScreen
//}

//@Composable
//fun nonScaledSp(size: Int): TextUnit {
//    val fontScale = LocalDensity.current.fontScale
//    return (size / fontScale).sp
//}

fun openDonate(context: Context) {
    val url = "https://pay.cloudtips.ru/p/2d71d3e5"
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}

enum class LoggerType {
    Error, Info, Debug
}

fun logger(loggerType: LoggerType, message: String) {
    when (loggerType) {
        LoggerType.Error -> Log.e("logger_tag", message)
        LoggerType.Info -> Log.i("logger_tag", message)
        LoggerType.Debug -> Log.d("logger_tag", message)
    }

}

fun format(text: Int): String {
    val ddd = DecimalFormat("###,###.##")
    return ddd.format(text)
}

fun format(text: Double): String {
    val ddd = DecimalFormat("###,###.##")
    return ddd.format(text)
}

fun formatMillisecondsToDaysHoursMinutes(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val totalMinutes = totalSeconds / 60
    val totalHours = totalMinutes / 60

    val days = totalHours / 24
    val hours = totalHours % 24
    val minutes = totalMinutes % 60

    return buildString {
        if (days > 0) {
            append("${days}д.")
        }
        if (hours > 0) {
            append("${hours}ч.")
        }
        if (minutes > 0) {
            append("${minutes}мин.")
        }
        if (isEmpty()) {
            append("0 мин")
        }
    }
}