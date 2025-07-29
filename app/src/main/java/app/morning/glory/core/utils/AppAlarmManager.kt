package app.morning.glory.core.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import app.morning.glory.core.extensions.toLocalTime
import app.morning.glory.core.service.AlarmService
import java.util.Calendar

object AppAlarmManager {
    private const val SLEEP_ALARM_REQUEST_CODE = 123

    /** Creates the PendingIntent for the alarm.
     * Used in alarm scheduling and cancelling
     */
    fun getPendingIntent(context: Context) : PendingIntent {
        val intent = Intent(context, AlarmService::class.java).apply {
            action = CustomActions.alarmTriggered(context)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getForegroundService(
            context,
            SLEEP_ALARM_REQUEST_CODE,
            intent,
            flags
        )
    }

    /**
     * Clones a Calendar instance and truncates its time to the minute.
     * Values of second and millisecond are replaced with 0.
     */
    fun getTruncatedTime(time: Calendar): Calendar {
        val newTime = time.clone() as Calendar
        newTime.set(Calendar.SECOND, 0)
        newTime.set(Calendar.MILLISECOND, 0)
        return newTime
    }

    fun scheduleSleepAlarm(context: Context, time: Calendar, isDaily: Boolean) {
        val truncatedTime = getTruncatedTime(time)
        if (isDaily) {
            val oldDaily = AppPreferences.dailyAlarm
            val localTime = truncatedTime.toLocalTime()
            if (localTime != oldDaily) {
                AppPreferences.dailyAlarm = localTime
            }
        }
        AppPreferences.sleetAlarmTime = truncatedTime

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            truncatedTime.timeInMillis,
            getPendingIntent(context)
        )
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        AppPreferences.sleetAlarmTime = null
        val pendingIntent = getPendingIntent(context)

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}