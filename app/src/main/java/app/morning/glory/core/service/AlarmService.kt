package app.morning.glory.core.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import app.morning.glory.core.audio.AlarmSoundPlayer
import app.morning.glory.core.extensions.applyLocalTime
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.alarm.AlarmActivity
import java.util.Calendar

class AlarmService : Service() {
    private lateinit var alarmSoundPlayer: AlarmSoundPlayer
    private var isRunning = false

    /// Creates the service and initiate the alarm sound player
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        alarmSoundPlayer = AlarmSoundPlayer(this)
    }

    /// On Trigger: Start playing the sound and show the notification
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON" -> {
                // TODO: Handle device boot - you might want to reschedule alarms here
                Log.d("AlarmService", "Device booted, rescheduling alarms")
                stopSelf()
                return START_NOT_STICKY
            }
            "${applicationContext.packageName}.ALARM_TRIGGERED" -> {
                if (isRunning) {
                    Log.d("AlarmService", "Alarm already running")
                    return START_STICKY
                }

                isRunning = true
                
                // Start in foreground with a notification
                startForeground(111, createNotification())
                
                // Start playing the alarm sound
                alarmSoundPlayer.playAlarm()

            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }

    /// If running, stop the alarm sound and remove the foreground notification,
    /// then stop the service
    fun stopAlarm() {
        if (isRunning) {
            alarmSoundPlayer.stopAlarm()
            manageReschedule()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            isRunning = false
        }
    }

    private fun manageReschedule()  {
        val context = applicationContext
        AppPreferences.init(context)
        val dailyAlarm = AppPreferences.dailyAlarm
        if (dailyAlarm != null) {
            val scheduleTime = Calendar.getInstance().applyLocalTime(dailyAlarm)
            scheduleTime.add(Calendar.HOUR_OF_DAY, 24)
            AppAlarmManager.scheduleAlarm(
                context,
                scheduleTime,
                isDaily = true
            )
        }
        AppPreferences.onceOffAlarm = null
    }

    /// Notification for foreground service and the Alarm
    private fun createNotification(): Notification {

        // Intent to open the AlarmActivity when the notification is clicked
        val fullScreenIntent = Intent(this, AlarmActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return AppNotificationManager.createAlarmNotification(
            context = this,
            fullScreenPendingIntent = fullScreenPendingIntent
        )
    }

    companion object {
        fun stopService(context: Context) {
            val intent = Intent(context, AlarmService::class.java)
            context.stopService(intent)
        }
    }
}
