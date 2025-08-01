package app.morning.glory.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        AppPreferences.init(context)
        AppAlarmManager.cancelAlarm(context, AlarmType.SLEEP)
        val manager = AppNotificationManager.getNotificationManager(context)
        manager.cancel(PreAlarmReceiver.STOP_ALARM_CODE)
    }
}