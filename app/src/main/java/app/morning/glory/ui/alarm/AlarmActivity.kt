package app.morning.glory.ui.alarm

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.morning.glory.core.service.AlarmService
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.theme.MorningGloryTheme

class AlarmActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreferences.init(this)

        // Turn on the screen and show on lock screen
        turnScreenOnAndShowWhenLocked()
        
        setContent {
            MorningGloryTheme {
                AlarmScreen(
                    onDismiss = {
                        AlarmService.stopService(this@AlarmActivity)
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