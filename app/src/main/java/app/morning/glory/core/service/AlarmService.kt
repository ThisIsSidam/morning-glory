package app.morning.glory.core.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import app.morning.glory.core.audio.AppSoundPlayer
import app.morning.glory.core.extensions.applyLocalTime
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.core.utils.CustomActions
import app.morning.glory.ui.alarm.AlarmActivity
import java.util.Calendar

class AlarmService : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): AlarmService = this@AlarmService
    }

    private val localBinder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? = localBinder

    private lateinit var appSoundPlayer: AppSoundPlayer
    private var isRunning = false
    private lateinit var alarmType: AlarmType
    private var snoozeCount: Int = 0

    /// Creates the service and initiate the alarm sound player
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        appSoundPlayer = AppSoundPlayer(this)
    }

    /// On Trigger: Start playing the sound and show the notification
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == CustomActions.alarmTriggered(applicationContext)) {
            if (isRunning) {
                Log.d("AlarmService", "Alarm already running")
                return START_STICKY
            }

            AppPreferences.init(applicationContext)

            val alarmTypeString = intent.getStringExtra(AppAlarmManager.ALARM_TYPE_EXTRA_KEY)
            alarmType = AlarmType.valueOfOrNull(alarmTypeString) ?: run {
                // Stop service if alarm type wasn't received.. bad intent
                appSoundPlayer.stop()
                stopSelf()
                return START_NOT_STICKY
            }

            snoozeCount = intent.getIntExtra(AppAlarmManager.SNOOZE_COUNT_EXTRA_KEY, 2)

            // Start in foreground with a notification
            startForeground(111, createNotification())

            // Start playing the alarm sound
            appSoundPlayer.playAlarm()

            isRunning = true
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissAlarm()
    }

    /// If running, stop the alarm sound and remove the foreground notification,
    /// then stop the service
    fun dismissAlarm() {
        if (!isRunning) return

        appSoundPlayer.release()

        // Clear the saved alarm time and reschedule in case of sleep alarm
        when (alarmType) {
            AlarmType.SLEEP -> {
                AppPreferences.sleepAlarmTime = null
                manageReschedule()
            }
            AlarmType.NAP -> AppPreferences.napAlarmTime = null
        }


        // Stop foreground and then service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isRunning = false
    }

    /// Check for daily time presence and set new alarm
    private fun manageReschedule()  {
        val dailyAlarm = AppPreferences.dailyAlarm
        if (dailyAlarm != null) {
            val scheduleTime = Calendar.getInstance().applyLocalTime(dailyAlarm)
            scheduleTime.add(Calendar.HOUR_OF_DAY, 24)
            AppAlarmManager.scheduleAlarm(
                applicationContext,
                scheduleTime,
                AlarmType.SLEEP
            )
        }
    }

    /**
     * Snooze the alarm for 10 minutes and dismiss it.
     */
    fun snoozeAndDismissAlarm() {
        if (!isRunning) return

        Log.d("AlarmService", "Snooze count: $snoozeCount")
        appSoundPlayer.stop()

        val time = Calendar.getInstance()
        time.add(Calendar.MINUTE, 2)
        AppAlarmManager.snoozeAlarm(applicationContext, time, AlarmType.SLEEP, snoozeCount+1)

        // Stop foreground and then service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isRunning = false
    }

    /// Notification for foreground service and the Alarm
    private fun createNotification(): Notification {

        // Intent to open the AlarmActivity when the notification is clicked
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).putExtra(
            AppAlarmManager.SNOOZE_COUNT_EXTRA_KEY, snoozeCount
        )
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
}
