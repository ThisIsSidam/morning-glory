package app.morning.glory.core.extensions

import android.content.SharedPreferences
import java.time.LocalTime
import java.util.Calendar

fun SharedPreferences.Editor.putTime(key: String, time: Calendar?) {
    if (time == null) {
        remove(key)
        return
    }
    putLong(key, time.timeInMillis)
}

fun SharedPreferences.getTime(key: String, defValue: Calendar?) : Calendar? {
    if (!contains(key)) return defValue
    val sec = getInt(key, -1)
    val timeLong : Long = getLong(key, -1L)
    return if (sec == -1) defValue else Calendar.getInstance().apply { timeInMillis = timeLong }
}

fun SharedPreferences.Editor.putLocalTime(key: String, time: LocalTime?) {
    if (time == null) {
        remove(key)
        return
    }
    putInt(key, time.toSecondOfDay())
}

fun SharedPreferences.getLocalTime(key: String, defValue: LocalTime?) : LocalTime? {
    if (!contains(key)) return defValue
    val sec = getInt(key, -1)
    return if (sec == -1) defValue else LocalTime.ofSecondOfDay(sec.toLong())
}