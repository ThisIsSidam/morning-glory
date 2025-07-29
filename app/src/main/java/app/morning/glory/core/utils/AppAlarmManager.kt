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
     * Used to truncate time to minutes..
     * Values of second and millisecond are replaced with 0
     */
    fun getTruncatedTime(time: Calendar) : Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, time.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }

    fun scheduleSleepAlarm(context: Context, time: Calendar, isDaily: Boolean) {
        if (isDaily) {
            val oldDaily = AppPreferences.dailyAlarm
            val localTime = time.toLocalTime()
            if (localTime != oldDaily) {
                AppPreferences.dailyAlarm = localTime
            }
        }
        AppPreferences.sleetAlarmTime = time

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getTruncatedTime(time).timeInMillis,
            getPendingIntent(context)
        )
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(context)

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}