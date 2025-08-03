package app.morning.glory.core.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.morning.glory.R
import app.morning.glory.core.extensions.friendly
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import java.util.Calendar

/**
 * Registers a follow-up alarm and shows a notification with which the user
 * can cancel that follow-up up alarm
 */
class FollowUpNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        Log.d("FollowUpNotificationReceiver", "running")

        AppPreferences.init(context)

        // Create time instance 15 minutes from now Register follow up alarm
        val time = Calendar.getInstance()
        time.add(Calendar.MINUTE, 15)

        Log.d("FollowUpNotificationReceiver", "Follow up time: ${time.friendly(context)}")

        AppAlarmManager.scheduleAlarm(context, time, AlarmType.FOLLOW_UP)

        // Create intent for cancelling it
        val actionIntent = Intent(context, StopAlarmReceiver::class.java).putExtra(
            AppAlarmManager.alarmTypeExtraKey, AlarmType.FOLLOW_UP.toString()
        )

        val actionPendingIntent = PendingIntent.getBroadcast(
            context,
            FOLLOW_UP_ALARM_CODE,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = AppNotificationManager.createActionNotification(
            context,
            actionPendingIntent,
            contentText = "You're awake, right? RIGHT??",
            actionText = "Yes",
            actionIcon = R.drawable.outline_check_small_24
        )
        val manager : NotificationManager = AppNotificationManager.getNotificationManager(context)
        manager.notify(FOLLOW_UP_ALARM_CODE, notification)
        Log.d("FollowUpNotificationReceiver", "notified")
    }

    companion object {
        /**
         * A Code used both as pending intent's request code
         * and the notification's id.
         */
        const val FOLLOW_UP_ALARM_CODE = 5123
    }
}