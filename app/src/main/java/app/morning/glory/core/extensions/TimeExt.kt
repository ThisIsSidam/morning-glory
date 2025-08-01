package app.morning.glory.core.extensions

import android.content.Context
import android.text.format.DateFormat
import java.time.LocalTime
import java.util.Calendar
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

private fun Calendar.isSameDay(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}

private fun Calendar.isYesterday(now: Calendar): Boolean {
    val yesterday = (now.clone() as Calendar).apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return this.isSameDay(yesterday)
}

private fun Calendar.isTomorrow(now: Calendar): Boolean {
    val tomorrow = (now.clone() as Calendar).apply {
        add(Calendar.DAY_OF_YEAR, 1)
    }
    return this.isSameDay(tomorrow)
}

fun Calendar.friendly(context: Context): String {
    val now = Calendar.getInstance()

    val dateText = when {
        this.isSameDay(now) -> "Today"
        this.isYesterday(now) -> "Yesterday"
        this.isTomorrow(now) -> "Tomorrow"

        else -> {
            // This format flag shows the date and year, respecting locale.
            // e.g., "December 31, 2025" or "31 December 2025"
            val flags = android.text.format.DateUtils.FORMAT_SHOW_DATE or android.text.format.DateUtils.FORMAT_SHOW_YEAR
            android.text.format.DateUtils.formatDateTime(context, this.timeInMillis, flags)
        }
    }

    // Determine the time part of the string, respecting 12/24-hour settings
    val timeFormat = if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm a"
    val timeText = DateFormat.format(timeFormat, this)

    return "$dateText, $timeText"
}
