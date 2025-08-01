package app.morning.glory.core.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.morning.glory.core.notifications.AppNotificationManager

class PreAlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val actionIntent = Intent(context, StopAlarmReceiver::class.java)

        val actionPendingIntent = PendingIntent.getBroadcast(
            context,
            STOP_ALARM_CODE,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = AppNotificationManager.createPreAlarmNotification(context, actionPendingIntent)
        val manager : NotificationManager = AppNotificationManager.getNotificationManager(context)
        manager.notify(STOP_ALARM_CODE, notification)
    }

    companion object {
        /**
         * A Code used both as pending intent's request code
         * and the notification's id.
         */
        const val STOP_ALARM_CODE = 5123
    }
}