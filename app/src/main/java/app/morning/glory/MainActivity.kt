package app.morning.glory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.morning.glory.core.notifications.NotificationChannelType
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.home.HomeScreen
import app.morning.glory.ui.theme.MorningGloryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppPreferences.init(this)
        NotificationChannelType.createAllChannels(this)

        setContent {
            MorningGloryTheme {
                HomeScreen()
            }
        }
    }
}
