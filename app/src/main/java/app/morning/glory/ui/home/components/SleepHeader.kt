package app.morning.glory.ui.home.components

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.formattedDuration
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

        NextSleepAlarmText()
    }
}

@Composable
fun NextSleepAlarmText() {
    var nextAlarmTime by remember {mutableStateOf(AppPreferences.sleepAlarmTime)}
    var showDuration by remember { mutableStateOf(AppPreferences.displaySleepDuration) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.ALARM_TIME) {
                nextAlarmTime = AppPreferences.sleepAlarmTime
            } else if (key == AppPreferences.DISPLAY_SLEEP_DURATION_HINT_KEY) {
                showDuration = AppPreferences.displaySleepDuration
            }
        }

        AppPreferences.registerListener(listener)
        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    val alarmTime = nextAlarmTime
    val textToShow = if (alarmTime == null) {
        "No alarm set!"
    } else {
        when (showDuration) {
            true -> "Alarm in ${alarmTime.formattedDuration()}"
            false -> "Next alarm at ${alarmTime.toReadable()}"
        }
    }

    Text(
        text = textToShow,
        modifier = Modifier.clickable(
            enabled = alarmTime != null
        ) {
            AppPreferences.displaySleepDuration = !showDuration
        }
    )
}