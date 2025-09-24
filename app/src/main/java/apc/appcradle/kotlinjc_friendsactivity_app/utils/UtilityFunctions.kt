package apc.appcradle.kotlinjc_friendsactivity_app.utils

import android.content.Context
import android.content.Intent
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
