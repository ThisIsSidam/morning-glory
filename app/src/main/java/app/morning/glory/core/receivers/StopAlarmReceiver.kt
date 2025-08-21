package app.morning.glory.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.morning.glory.core.extensions.applyLocalTime
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import java.time.LocalTime
import java.util.Calendar

/**
 * Finds alarm type in the intent.extras..
 * If found.. cancels that alarm
 */
class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val alarmTypeString = intent?.getStringExtra(AppAlarmManager.ALARM_TYPE_EXTRA_KEY)
        val alarmType = AlarmType.valueOfOrNull(alarmTypeString) ?: return

        AppPreferences.init(context)
        when(alarmType) {
            AlarmType.SLEEP -> {
                val dailyAlarm : LocalTime? = AppPreferences.dailyAlarm
                if (dailyAlarm == null) { // Cancel once-off alarm
                    AppAlarmManager.cancelAlarm(context, AlarmType.SLEEP)
                } else { // Reschedule daily alarm
                    val scheduleTime = Calendar.getInstance().applyLocalTime(dailyAlarm)
                    scheduleTime.add(Calendar.HOUR_OF_DAY, 24)
                    AppAlarmManager.scheduleAlarm(context, scheduleTime, AlarmType.SLEEP)
                }
            }
            AlarmType.NAP -> AppAlarmManager.cancelAlarm(context, AlarmType.NAP)
        }
        val manager = AppNotificationManager.getNotificationManager(context)
        manager.cancel(WakeCheckAlarmReceiver.WAKE_CHECK_ALARM_CODE)
    }
}