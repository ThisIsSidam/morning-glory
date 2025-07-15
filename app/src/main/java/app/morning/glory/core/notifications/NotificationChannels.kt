package app.morning.glory.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import app.morning.glory.R

enum class NotificationChannelType(
    val id: String,
    @StringRes private val nameResId: Int,
    @StringRes private val descriptionResId: Int,
    val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
) {
    ALARMS(
        "alarms",
        R.string.channel_alarms_name,
        R.string.channel_alarms_description,
        NotificationManager.IMPORTANCE_HIGH
    ),
    GENERAL(
        "general",
        R.string.channel_general_name,
        R.string.channel_general_description
    );

    fun getName(context: Context): String = context.getString(nameResId)
    fun getDescription(context: Context): String = context.getString(descriptionResId)

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun createAllChannels(context: Context) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            
            values().forEach { channelType ->
                val channel = NotificationChannel(
                    channelType.id,
                    channelType.getName(context),
                    channelType.importance
                ).apply {
                    description = channelType.getDescription(context)
                    when (channelType) {
                        ALARMS -> {
                            enableVibration(true)
                            enableLights(true)
                        }
                        GENERAL -> {
                            enableVibration(false)
                        }
                    }
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
