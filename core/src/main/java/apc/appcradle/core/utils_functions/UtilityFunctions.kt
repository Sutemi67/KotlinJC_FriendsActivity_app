package apc.appcradle.core.utils_functions

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import androidx.core.net.toUri
import java.util.Calendar
import kotlin.math.roundToInt

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

fun openDonate(context: Context) {
    val url = "https://pay.cloudtips.ru/p/2d71d3e5"
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}

fun format(text: Int): String {
    val ddd = DecimalFormat("###,###.##")
    return ddd.format(text)
}

fun format(text: Double): String {
    val ddd = DecimalFormat("###,###.##")
    return ddd.format(text)
}

fun kkalCalc(userStepLength: Double, stepCount: Int): IntRange {
    val firstValue = (50 * (stepCount * userStepLength / 2500)).roundToInt()
    val secondValue = (75 * (stepCount * userStepLength / 2500)).roundToInt()
    return IntRange(firstValue, secondValue)
}