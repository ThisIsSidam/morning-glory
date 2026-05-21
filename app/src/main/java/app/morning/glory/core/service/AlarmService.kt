package app.morning.glory.core.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresApi
import app.morning.glory.core.audio.AppSoundPlayer
import app.morning.glory.core.extensions.applyLocalTime
import app.morning.glory.core.notifications.AppNotificationManager
import app.morning.glory.core.receivers.WakeCheckAlarmReceiver
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

    override fun onBind(intent: Intent?): IBinder = localBinder

    private lateinit var appSoundPlayer: AppSoundPlayer
    private var vibrator: Vibrator? = null

    companion object {
        var isRunning = false
        var activeAlarmType: AlarmType? = null
        var activeSnoozeCount: Int = 0
    }

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
            activeAlarmType = AlarmType.valueOfOrNull(alarmTypeString) ?: run {
                // Stop service if alarm type wasn't received.. bad intent
                appSoundPlayer.stop()
                stopSelf()
                return START_NOT_STICKY
            }

            activeSnoozeCount = intent.getIntExtra(AppAlarmManager.SNOOZE_COUNT_EXTRA_KEY, 0)

            // Start in foreground with a notification
            startForeground(111, createNotification())

            // Cancel any pre-alarm notifications
            AppNotificationManager.getNotificationManager(applicationContext)
                .cancel(WakeCheckAlarmReceiver.WAKE_CHECK_ALARM_CODE)

            // Start playing the alarm sound
            playAlarmSound()

            // Start vibration
            startVibration()

            isRunning = true
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissAlarm()
    }

    private fun startVibration() {
        if (AppPreferences.vibrationMode == "None") return

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = when (AppPreferences.vibrationMode) {
            "Heavy" -> longArrayOf(0, 800, 400)
            else -> longArrayOf(0, 400, 800) // Light
        }

        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

    /// If randomize is on, we pick a random ringtone and play
    /// If not, use the selected ringtone
    private fun playAlarmSound() {
        val ringtoneUri = if (AppPreferences.randomizeRingtones) {
            val list = AppPreferences.getRingtoneList()
            val allRingtones = list.map { it.uri } + AppSoundPlayer.getDefaultRingtoneUri(this)
            allRingtones.random()
        } else {
            AppPreferences.selectedRingtone ?: AppSoundPlayer.getDefaultRingtoneUri(this)
        }
        appSoundPlayer.playAlarm(ringtoneUri)
    }

    /// If running, stop the alarm sound and remove the foreground notification,
    /// then stop the service
    fun dismissAlarm() {
        if (!isRunning) return

        appSoundPlayer.release()
        stopVibration()

        // Clear the saved alarm time and reschedule in case of sleep alarm
        when (activeAlarmType) {
            AlarmType.SLEEP -> {
                AppPreferences.sleepAlarmTime = null
                manageReschedule()
            }

            AlarmType.NAP -> AppPreferences.napAlarmTime = null
            null -> {}
        }


        // Stop foreground and then service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isRunning = false
        activeAlarmType = null
        activeSnoozeCount = 0
    }

    /// Check for daily time presence and set new alarm
    private fun manageReschedule() {
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
     * Snooze the alarm and dismiss it.
     */
    fun snoozeAndDismissAlarm() {
        if (!isRunning) return

        Log.d("AlarmService", "Snooze count: $activeSnoozeCount")
        appSoundPlayer.stop()
        stopVibration()

        val time = Calendar.getInstance()
        time.add(Calendar.MINUTE, AppPreferences.snoozeDurationMinutes)
        AppAlarmManager.snoozeAlarm(
            applicationContext,
            time,
            activeAlarmType!!,
            activeSnoozeCount + 1
        )

        // Stop foreground and then service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isRunning = false
        activeAlarmType = null
        activeSnoozeCount = 0
    }

    /// Notification for foreground service and the Alarm
    private fun createNotification(): Notification {

        // Intent to open the AlarmActivity when the notification is clicked
        val fullScreenIntent = Intent(this, AlarmActivity::class.java)
            .putExtra(AppAlarmManager.ALARM_TYPE_EXTRA_KEY, activeAlarmType?.name)
            .putExtra(AppAlarmManager.SNOOZE_COUNT_EXTRA_KEY, activeSnoozeCount)
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
