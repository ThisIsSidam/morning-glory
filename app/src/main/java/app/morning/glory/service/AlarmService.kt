package app.morning.glory.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import app.morning.glory.audio.AlarmSoundPlayer
import app.morning.glory.notifications.AppNotificationManager
import app.morning.glory.ui.alarm.AlarmActivity

class AlarmService : Service() {
    private lateinit var alarmSoundPlayer: AlarmSoundPlayer
    private var isRunning = false
    private lateinit var stopReceiver: BroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        alarmSoundPlayer = AlarmSoundPlayer(this)
        registerStopReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON" -> {
                // Handle device boot - you might want to reschedule alarms here
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
        unregisterReceiver(stopReceiver)
        stopAlarm()
    }

    fun stopAlarm() {
        if (isRunning) {
            alarmSoundPlayer.stopAlarm()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            isRunning = false
        }
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(ACTION_STOP_ALARM)
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return AppNotificationManager.createAlarmNotification(
            context = this,
            stopPendingIntent = stopPendingIntent,
            fullScreenPendingIntent = fullScreenPendingIntent
        )
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun registerStopReceiver() {
        stopReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ACTION_STOP_ALARM) {
                    Log.d("AlarmService", "Stop alarm action received")
                    stopAlarm()
                }
            }
        }
        
        val filter = IntentFilter(ACTION_STOP_ALARM)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            registerReceiver(stopReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(stopReceiver, filter) // older method without flags
        }
    }

    companion object {
        const val ACTION_STOP_ALARM = "app.morning.glory.action.STOP_ALARM"

        fun stopService(context: Context) {
            val intent = Intent(context, AlarmService::class.java)
            context.stopService(intent)
        }
    }
}
