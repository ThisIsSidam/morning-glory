package app.morning.glory

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import app.morning.glory.core.notifications.NotificationChannelType
import app.morning.glory.core.service.AlarmService
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.alarm.AlarmActivity
import app.morning.glory.ui.home.HomeScreen
import app.morning.glory.ui.theme.MorningGloryTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Handle permission result */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppPreferences.init(this)

        if (AlarmService.isRunning) {
            val alarmType = AlarmService.activeAlarmType
            val snoozeCount = AlarmService.activeSnoozeCount
            if (alarmType != null) {
                val alarmIntent = Intent(this, AlarmActivity::class.java).apply {
                    putExtra(AppAlarmManager.ALARM_TYPE_EXTRA_KEY, alarmType.name)
                    putExtra(AppAlarmManager.SNOOZE_COUNT_EXTRA_KEY, snoozeCount)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(alarmIntent)
                finish()
                return
            }
        }

        NotificationChannelType.createAllChannels(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        setContent {
            MorningGloryTheme {
                HomeScreen()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
