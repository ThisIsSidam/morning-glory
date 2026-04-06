package app.morning.glory.core.extensions

import android.content.SharedPreferences
import java.time.LocalTime
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun SharedPreferences.Editor.putTime(key: String, time: Calendar?) {
    if (time == null) {
        remove(key)
        return
    }
    putLong(key, time.timeInMillis)
}

fun SharedPreferences.getTime(key: String, defValue: Calendar?): Calendar? {
    if (!contains(key)) return defValue
    val timeLong: Long = getLong(key, -1L)
    return if (timeLong == -1L) defValue else Calendar.getInstance()
        .apply { timeInMillis = timeLong }
}

fun SharedPreferences.Editor.putLocalTime(key: String, time: LocalTime?) {
    if (time == null) {
        remove(key)
        return
    }
    putInt(key, time.toSecondOfDay())
}

fun SharedPreferences.getLocalTime(key: String, defValue: LocalTime?): LocalTime? {
    if (!contains(key)) return defValue
    val sec = getInt(key, -1)
    return if (sec == -1) defValue else LocalTime.ofSecondOfDay(sec.toLong())
}


fun SharedPreferences.Editor.putDuration(key: String, dur: Duration?) {
    if (dur == null) {
        remove(key)
        return
    }
    putLong(key, dur.inWholeMilliseconds)
}

fun SharedPreferences.getDuration(key: String, defValue: Duration?): Duration? {
    if (!contains(key)) return defValue
    val ms: Long = getLong(key, -1)
    return if (ms == -1L) defValue else ms.milliseconds
}