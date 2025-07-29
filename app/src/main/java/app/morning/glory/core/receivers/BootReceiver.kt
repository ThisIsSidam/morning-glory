package app.morning.glory.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.morning.glory.core.extensions.toReadable
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Running")
            AppPreferences.init(context)
            rescheduleAlarms(context)
        }
    }

    private fun rescheduleAlarms(context: Context) {
        val alarmTime = AppPreferences.sleetAlarmTime
        if (alarmTime != null) {
            // isDaily is used to save the localTime in prefs that is already
            // done when the alarm was initially scheduled, keeping false
            AppAlarmManager.scheduleSleepAlarm(context, alarmTime, isDaily = false)
            Log.d("BootReceiver", "Rescheduling after reboot: ${alarmTime.toReadable()}")
        } else {
            Log.d("BootReceiver", "Nothing to reschedule")
        }

    }
}