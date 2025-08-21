package app.morning.glory.core.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.morning.glory.R
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager

class WakeCheckAlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val actionIntent = Intent(context, StopAlarmReceiver::class.java).putExtra(
            AppAlarmManager.ALARM_TYPE_EXTRA_KEY, AlarmType.SLEEP.toString()
        )

        val actionPendingIntent = PendingIntent.getBroadcast(
            context,
            WAKE_CHECK_ALARM_CODE,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = AppNotificationManager.createActionNotification(
            context,
            actionPendingIntent,
            contentText = "Oi! Dismiss the alarm if you're already awake.",
            actionText = "Dismiss Alarm",
            actionIcon = R.drawable.round_stop_24
        )
        val manager : NotificationManager = AppNotificationManager.getNotificationManager(context)
        manager.notify(WAKE_CHECK_ALARM_CODE, notification)
    }

    companion object {
        /**
         * A Code used both as pending intent's request code
         * and the notification's id.
         */
        const val WAKE_CHECK_ALARM_CODE = 5123
    }
}