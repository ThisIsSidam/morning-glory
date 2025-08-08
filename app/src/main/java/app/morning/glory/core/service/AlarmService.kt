package app.morning.glory.core.service

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import app.morning.glory.core.audio.AlarmSoundPlayer
import app.morning.glory.core.extensions.applyLocalTime
import app.morning.glory.core.extensions.friendly
import app.morning.glory.core.extensions.truncateToSeconds
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.receivers.FollowUpNotificationReceiver
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

    private lateinit var alarmSoundPlayer: AlarmSoundPlayer
    private var isRunning = false
    private lateinit var alarmType: AlarmType
    private var snoozeCount: Int = 0

    /// Creates the service and initiate the alarm sound player
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        alarmSoundPlayer = AlarmSoundPlayer(this)
    }

    /// On Trigger: Start playing the sound and show the notification
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == CustomActions.alarmTriggered(applicationContext)) {
            if (isRunning) {
                Log.d("AlarmService", "Alarm already running")
                return START_STICKY
            }

            val alarmTypeString = intent.getStringExtra(AppAlarmManager.ALARM_TYPE_EXTRA_KEY)
            alarmType = AlarmType.valueOfOrNull(alarmTypeString) ?: run {
                // Stop service if alarm type wasn't received.. bad intent
                alarmSoundPlayer.stopAlarm()
                stopSelf()
                return START_NOT_STICKY
            }

            snoozeCount = intent.getIntExtra(AppAlarmManager.SNOOZE_COUNT_EXTRA_KEY, 2)

            // Start in foreground with a notification
            startForeground(111, createNotification())

            isRunning = true

            // Start playing the alarm sound
            alarmSoundPlayer.playAlarm()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }

    /// If running, stop the alarm sound and remove the foreground notification,
    /// then stop the service
    fun stopAlarm() {
        if (!isRunning) return

        // Initialized preferences
        val context = applicationContext
        AppPreferences.init(context)

        alarmSoundPlayer.stopAlarm()

        // Clear the saved alarm time and reschedule in case of sleep alarm
        when (alarmType) {
            AlarmType.SLEEP -> {
                AppPreferences.sleepAlarmTime = null
                registerFollowUpNotification(context)
                manageReschedule(context)
            }
            AlarmType.NAP -> AppPreferences.napAlarmTime = null
            AlarmType.FOLLOW_UP -> AppPreferences.followUpAlarmTime = null
        }


        // Stop foreground and then service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isRunning = false
    }

    /// Check for daily time presence and set new alarm
    private fun manageReschedule(context: Context)  {
        val dailyAlarm = AppPreferences.dailyAlarm
        if (dailyAlarm != null) {
            val scheduleTime = Calendar.getInstance().applyLocalTime(dailyAlarm)
            scheduleTime.add(Calendar.HOUR_OF_DAY, 24)
            AppAlarmManager.scheduleAlarm(
                context,
                scheduleTime,
                AlarmType.SLEEP
            )
        }
    }

    fun snoozeAlarm() {
        // TODO: Complete method
        val time = Calendar.getInstance()
        time.add(Calendar.MINUTE, 10)
        AppAlarmManager.snoozeAlarm(applicationContext, time, AlarmType.SLEEP, snoozeCount+1)
    }

    private fun registerFollowUpNotification(context: Context) {
        val time = Calendar.getInstance()
        time.add(Calendar.MINUTE, 15)

        Log.d("AlarmService", "follow up time: ${time.friendly(context)}")
        val intent = Intent(context, FollowUpNotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            FOLLOW_UP_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.truncateToSeconds().timeInMillis,
            pendingIntent
        )
        Log.d("AlarmService", "Schedule Complete")
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

    companion object {
        private const val FOLLOW_UP_REQUEST_CODE = 1500
    }
}
