package app.morning.glory.core.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import app.morning.glory.core.extensions.toLocalTime
import app.morning.glory.core.extensions.truncateToSeconds
import app.morning.glory.core.receivers.WakeCheckAlarmReceiver
import app.morning.glory.core.service.AlarmService
import java.util.Calendar

enum class AlarmType(val requestCode: Int) {
    SLEEP(123),
    NAP(111),
    ;

    companion object {
        fun valueOfOrNull(name: String?): AlarmType? =
            name?.let { entries.find { it.name == name } }
    }

    fun updatePrefs(time: Calendar?) {
        when (this) {
            SLEEP -> AppPreferences.sleepAlarmTime = time
            NAP -> AppPreferences.napAlarmTime = time
        }
    }
}

object AppAlarmManager {

    const val ALARM_TYPE_EXTRA_KEY : String = "alarm-type"
    const val SNOOZE_COUNT_EXTRA_KEY : String = "snooze_count"
    private const val WAKE_CHECK_ALARM_REQUEST_CODE = 2345

    /**
     * Creates the PendingIntent for the alarm.
     * Used in alarm scheduling and cancelling
     */
    fun getAlarmPendingIntent(
        context: Context, type: AlarmType, snoozeCount: Int = 0
    ) : PendingIntent {
        val intent = Intent(context, AlarmService::class.java).apply {
            action = CustomActions.alarmTriggered(context)
            putExtra(ALARM_TYPE_EXTRA_KEY, type.toString())
            putExtra(SNOOZE_COUNT_EXTRA_KEY, snoozeCount)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getForegroundService(
            context,
            type.requestCode,
            intent,
            flags
        )
    }

    fun getWakeCheckAlarmPendingIntent(context: Context) : PendingIntent {
        val intent = Intent(context, WakeCheckAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            WAKE_CHECK_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * If the local time of calendar instance is different, it saves the new time
     * and then schedules the alarm
     */
    fun scheduleDailyAlarm(context: Context, time: Calendar) {
        val truncatedTime = time.truncateToSeconds()
        val oldDaily = AppPreferences.dailyAlarm
        val localTime = truncatedTime.toLocalTime()
        if (localTime != oldDaily) {
            AppPreferences.dailyAlarm = localTime
        }
        scheduleAlarm(context, time, AlarmType.SLEEP)
    }

    fun scheduleAlarm(context: Context, time: Calendar, type: AlarmType, ) {
        val truncatedTime = time.truncateToSeconds()
        type.updatePrefs(truncatedTime)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            truncatedTime.timeInMillis,
            getAlarmPendingIntent(context, type)
        )

        // 30 Minutes before main alarm time, pre-alarm notification will be shown
        // Only for sleep alarms
        if (type == AlarmType.SLEEP) {
            truncatedTime.add(Calendar.MINUTE, -AppPreferences.wakeCheckAlarmTime)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                truncatedTime.timeInMillis,
                getWakeCheckAlarmPendingIntent(context)
            )
        }
    }

    fun snoozeAlarm(context: Context, time: Calendar, type: AlarmType, snoozeCount: Int ) {
        val truncatedTime = time.truncateToSeconds()
        type.updatePrefs(truncatedTime)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            truncatedTime.timeInMillis,
            getAlarmPendingIntent(context, type, snoozeCount)
        )
    }

    fun cancelAlarm(context: Context, type: AlarmType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        type.updatePrefs(null)

        val pendingIntent = getAlarmPendingIntent(context, type)

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}