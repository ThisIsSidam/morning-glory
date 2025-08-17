package app.morning.glory.ui.alarm

import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import app.morning.glory.core.service.AlarmService
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.theme.MorningGloryTheme

class AlarmActivity : ComponentActivity() {

    private lateinit var alarmService: AlarmService
    private var isBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AlarmService.LocalBinder
            alarmService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, AlarmService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreferences.init(this)

        // Turn on the screen and show on lock screen
        turnScreenOnAndShowWhenLocked()

        // To make activity edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val snoozeCount = intent.getIntExtra(
            AppAlarmManager.SNOOZE_COUNT_EXTRA_KEY,
            AppPreferences.DEFAULT_MAX_SNOOZE_COUNT
        )

        setContent {
            MorningGloryTheme {
                AlarmScreen(
                    onDismiss = {
                        if (isBound) {
                            alarmService.stopSelf()
                        }
                        finish()
                    },
                    snoozeCount = snoozeCount,
                    onSnooze = {
                        if (isBound) {
                            alarmService.snoozeAndDismissAlarm()
                        }
                        finish()
                    }
                )
            }
        }
    }
    
    private fun turnScreenOnAndShowWhenLocked() {
        // Set window flags to show on lock screen and keep screen on
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
        
        // Dismiss keyguard if needed
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            keyguardManager.requestDismissKeyguard(this, null)
        }
    }
}