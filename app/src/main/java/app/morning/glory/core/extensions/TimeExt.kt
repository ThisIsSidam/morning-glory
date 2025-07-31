package app.morning.glory.core.extensions

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun Calendar.toLocalTime() : LocalTime {
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

/**
 * Clones a Calendar instance and truncates its time to the minute.
 * Values of second and millisecond are replaced with 0.
 */
fun Calendar.truncateToSeconds(): Calendar {
    val newTime = clone() as Calendar
    newTime.set(Calendar.SECOND, 0)
    newTime.set(Calendar.MILLISECOND, 0)
    return newTime
}

fun Calendar.difference(other: Calendar) : Duration {
    val millisInt = other.timeInMillis - timeInMillis
    return millisInt.milliseconds
}

fun Calendar.formattedDuration(other : Calendar = Calendar.getInstance()) : String {
    val dur = other.difference(this)

    return dur.toComponents { hours, minutes, seconds, nanoseconds ->
        if (hours == 0L) {
            return "$minutes Min"
        }
        return "$hours Hr $minutes Min"
    }
}

fun Calendar.isInPast() : Boolean {
    return this.before(Calendar.getInstance())
}