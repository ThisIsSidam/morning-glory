package app.morning.glory.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import app.morning.glory.MainActivity
import app.morning.glory.R

object AppNotificationManager {
    private fun getNotificationManager(context: Context): NotificationManager {
        return ContextCompat.getSystemService(context, NotificationManager::class.java)!!
    }

    fun createAlarmNotification(
        context: Context,
        stopPendingIntent: PendingIntent,
        fullScreenPendingIntent: PendingIntent
    ): Notification {
        val contentTitle = context.getString(R.string.alarm_notification_title)
        val contentText = context.getString(R.string.alarm_notification_content)
        
        return NotificationCompat.Builder(context, NotificationChannelType.ALARMS.id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                context.getString(R.string.stop_alarm),
                stopPendingIntent
            )
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
}
