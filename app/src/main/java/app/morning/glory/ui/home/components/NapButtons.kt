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
import androidx.compose.ui.unit.dp
import app.morning.glory.core.utils.AppPreferences
import java.util.Calendar

@Composable
fun NapButtons(selectedTime: Calendar) {
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
                    // TODO: Cancel nap alarm
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text("Cancel")
            }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {
                // TODO: Set/Update nap alarm
            },
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(if (napTime != null) "Update" else "Set Alarm")
        }
    }
}