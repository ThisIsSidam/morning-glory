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
fun NapHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nap",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        NextNapAlarmText()
    }
}

@Composable
fun NextNapAlarmText() {
    var nextAlarmTime by remember { mutableStateOf(AppPreferences.napAlarmTime) }
    var showDuration by remember { mutableStateOf(AppPreferences.displayNapDuration) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.NAP_TIME) {
                nextAlarmTime = AppPreferences.napAlarmTime
            } else if (key == AppPreferences.DISPLAY_NAP_DURATION_HINT_KEY) {
                showDuration = AppPreferences.displayNapDuration
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
            AppPreferences.displayNapDuration = !showDuration
        }
    )
}