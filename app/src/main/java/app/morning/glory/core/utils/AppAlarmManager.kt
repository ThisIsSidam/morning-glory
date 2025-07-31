package app.morning.glory.core.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import app.morning.glory.core.extensions.toLocalTime
import app.morning.glory.core.extensions.truncateToSeconds
import app.morning.glory.core.service.AlarmService
import java.util.Calendar

enum class AlarmType(val requestCode: Int) {
    SLEEP(123),
    NAP(111)
    ;

    companion object {
        fun valueOfOrNull(name: String?): AlarmType? =
            name?.let { entries.find { it.name == name } }
    }
}

object AppAlarmManager {

    val alarmTypeExtraKey : String = "alarm-type"

    /** Creates the PendingIntent for the alarm.
     * Used in alarm scheduling and cancelling
     */
    fun getPendingIntent(context: Context, type: AlarmType) : PendingIntent {
        val intent = Intent(context, AlarmService::class.java).apply {
            action = CustomActions.alarmTriggered(context)
        }.putExtra(alarmTypeExtraKey,  type.toString())

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getForegroundService(
            context,
            type.requestCode,
            intent,
            flags
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

    fun scheduleAlarm(context: Context, time: Calendar, type: AlarmType) {
        val truncatedTime = time.truncateToSeconds()
        when (type) {
            AlarmType.SLEEP -> AppPreferences.sleepAlarmTime = truncatedTime
            AlarmType.NAP -> AppPreferences.napAlarmTime = truncatedTime
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            truncatedTime.timeInMillis,
            getPendingIntent(context, type)
        )
    }

    fun cancelAlarm(context: Context, type: AlarmType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        when (type) {
            AlarmType.SLEEP -> AppPreferences.sleepAlarmTime = null
            AlarmType.NAP -> AppPreferences.napAlarmTime = null
        }

        val pendingIntent = getPendingIntent(context, type)

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}