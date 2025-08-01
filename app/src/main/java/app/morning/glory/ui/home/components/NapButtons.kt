package app.morning.glory.ui.home.components

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.friendly
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import java.util.Calendar

@Composable
fun NapButtons(time: Calendar) {

    val context = LocalContext.current
    var napTime by remember { mutableStateOf(AppPreferences.napAlarmTime) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.NAP_TIME) {
                napTime = AppPreferences.napAlarmTime
            }
        }

        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    Row {
        if (napTime != null)
            Button(
                onClick = {
                    AppAlarmManager.cancelAlarm(context, AlarmType.NAP)
                    context.toast("Cancelled Alarm")
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text("Cancel")
            }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {
                AppAlarmManager.scheduleAlarm(context, time, AlarmType.NAP)
                context.toast("Scheduled time: ${time.friendly(context)}")
            },
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(if (napTime != null) "Update" else "Set Alarm")
        }
    }
}