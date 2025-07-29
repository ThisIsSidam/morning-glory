package app.morning.glory.ui.home.components

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.toReadable
import app.morning.glory.core.utils.AppPreferences

@Composable
fun SleepHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sleep",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        NextAlarmText()
    }
}

@Composable
fun NextAlarmText() {
    var nextAlarmTime = remember {mutableStateOf(AppPreferences.sleetAlarmTime)}

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.ALARM_TIME) {
                nextAlarmTime.value = AppPreferences.sleetAlarmTime
            }
        }

        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    Text(
        text = if (nextAlarmTime.value == null) "No alarm set!" else "Next alarm at ${nextAlarmTime.value!!.toReadable()}"
    )
}