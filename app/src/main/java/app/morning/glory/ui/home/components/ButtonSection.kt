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
import app.morning.glory.core.extensions.toReadable
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import java.util.Calendar

enum class ShowSheet {
    NONE,
    CANCEL,
    UPDATE
}

@Composable
fun ButtonSection(time: Calendar) {

    val context = LocalContext.current
    var showSheet by remember { mutableStateOf(ShowSheet.NONE) }
    var setAlarmTime by remember {mutableStateOf(AppPreferences.sleepAlarmTime) }
    var dailyAlarm by remember {mutableStateOf(AppPreferences.dailyAlarm)}

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.ALARM_TIME) {
                setAlarmTime = AppPreferences.sleepAlarmTime
            } else if (key == AppPreferences.DAILY_SLEEP_ALARM_KEY) {
                dailyAlarm = AppPreferences.dailyAlarm
            }
        }

        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    if (showSheet == ShowSheet.UPDATE) {
        AlarmUpdateSheet(
            isDailyAlarmSet = dailyAlarm != null,
            onDismissRequest = {
                showSheet = ShowSheet.NONE
            },
            onOneTimeAlarmClick = {
                AppAlarmManager.scheduleSleepAlarm(context, time, isDaily = false)
                context.toast("Scheduled time: ${time.toReadable()}")
                showSheet = ShowSheet.NONE
            },
            onDailyAlarmClick = {
                AppAlarmManager.scheduleSleepAlarm(context, time, isDaily = true)
                context.toast("Scheduled time: ${time.toReadable()}")
                showSheet = ShowSheet.NONE
            }
        )
    } else if (showSheet == ShowSheet.CANCEL) {
        AlarmCancellationSheet(
            dailyAlarm = dailyAlarm,
            setAlarmTime = setAlarmTime,
            onDismiss = { showSheet = ShowSheet.NONE }
        )
    }

    Row {
        if (setAlarmTime != null)
            Button(
                onClick = {
                    showSheet = ShowSheet.CANCEL
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text("Cancel")
            }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {
                showSheet = ShowSheet.UPDATE
            },
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(if (setAlarmTime != null) "Update" else "Set Alarm")
        }
    }
}