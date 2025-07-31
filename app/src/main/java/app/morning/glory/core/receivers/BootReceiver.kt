package app.morning.glory.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.morning.glory.core.extensions.toReadable
import app.morning.glory.core.utils.AlarmType
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
        val sleepAlarmTime = AppPreferences.sleepAlarmTime
        if (sleepAlarmTime != null) {
            AppAlarmManager.scheduleAlarm(context, sleepAlarmTime, AlarmType.SLEEP)
            Log.d("BootReceiver", "Rescheduling sleep alarm after reboot: ${sleepAlarmTime.toReadable()}")
        }

        val napAlarmTime = AppPreferences.napAlarmTime
        if (napAlarmTime != null) {
            AppAlarmManager.scheduleAlarm(context, napAlarmTime, AlarmType.NAP)
            Log.d("BootReceiver", "Rescheduling nap alarm after reboot: ${napAlarmTime.toReadable()}")
        }

    }
}