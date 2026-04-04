package app.morning.glory.core.extensions

import kotlin.time.Duration

fun Duration.friendly(): String {
    val totalSeconds = inWholeSeconds

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60

    return buildString {
        if (hours > 0) {
            append(hours)
            append(" hr")
            if (hours > 1) append("s")
        }

        if (minutes > 0) {
            if (isNotEmpty()) append(" ")
            append(minutes)
            append(" min")
            if (minutes > 1) append("s")
        }

        if (hours == 0L && minutes == 0L) {
            append("0 min")
        }
    }
}