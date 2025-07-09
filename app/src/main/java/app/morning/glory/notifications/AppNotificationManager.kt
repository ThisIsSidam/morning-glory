package app.morning.glory.notifications

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

    fun showNotification(
        context: Context,
        channel: NotificationChannelType,
        notificationId: Int,
        title: String,
        content: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        autoCancel: Boolean = true,
        intent: Intent? = null
    ) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent ?: Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(priority)
            .setAutoCancel(autoCancel)
            .setContentIntent(pendingIntent)
            .build()

        getNotificationManager(context).notify(notificationId, notification)
    }

    fun showAlarmNotification(context: Context) {
        showNotification(
            context = context,
            channel = NotificationChannelType.ALARMS,
            notificationId = 1,
            title = context.getString(R.string.alarm_notification_title),
            content = context.getString(R.string.alarm_notification_content),
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }
}
