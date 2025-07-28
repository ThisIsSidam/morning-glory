package app.morning.glory.core.extensions

import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

fun Calendar.toLocalTime() : LocalTime {
    Log.d("toLocalTime", "here")
    return LocalTime.of(
        get(Calendar.HOUR_OF_DAY),
        get(Calendar.MINUTE),
        get(Calendar.SECOND),
        // ms to ns
        get(Calendar.MILLISECOND) * 1_000_000
    )
}

fun Calendar.applyLocalTime(localTime: LocalTime): Calendar {
    this.set(Calendar.HOUR_OF_DAY, localTime.hour)
    this.set(Calendar.MINUTE, localTime.minute)
    this.set(Calendar.SECOND, localTime.second)
    // Convert nanoseconds to milliseconds
    this.set(Calendar.MILLISECOND, localTime.nano / 1_000_000)
    return this
}

fun Calendar.toReadable() : String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val readableDate = sdf.format(time)
    return readableDate
}