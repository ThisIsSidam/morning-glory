package app.morning.glory.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.morning.glory.notifications.AppNotificationManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "onReceive: ${intent.action}")
        
        when (intent.action) {
            "${context.packageName}.ALARM_TRIGGERED" -> {
                Log.d("AlarmReceiver", "Alarm triggered!")
                AppNotificationManager.showAlarmNotification(context)
            }
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON" -> {
                Log.d("AlarmReceiver", "Device booted")
                // TODO: Reschedule alarms after reboot
            }
        }
    }
}
