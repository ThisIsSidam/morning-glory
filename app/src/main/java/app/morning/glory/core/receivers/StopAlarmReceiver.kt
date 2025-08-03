package app.morning.glory.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences

/**
 * Finds alarm type in the intent.extras..
 * If found.. cancels that alarm
 */
class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val alarmTypeString = intent?.getStringExtra(AppAlarmManager.alarmTypeExtraKey)
        val alarmType = AlarmType.valueOfOrNull(alarmTypeString) ?: return

        AppPreferences.init(context)
        AppAlarmManager.cancelAlarm(context, alarmType)
        val manager = AppNotificationManager.getNotificationManager(context)
        manager.cancel(PreAlarmReceiver.PRE_ALARM_CODE)
    }
}