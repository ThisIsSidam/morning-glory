package app.morning.glory.ui.home.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.applyLocalTime
import app.morning.glory.core.extensions.toLocalTime
import app.morning.glory.core.extensions.toReadable
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import java.time.LocalTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmCancellationSheet(
    dailyAlarm: LocalTime?,
    setAlarmTime: Calendar?,
    onDismiss: () -> Unit,
) {
    require(setAlarmTime != null, { "No next alarm set! Nothing to cancel" })

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    fun onCancelOnceOff() {
        AppAlarmManager.cancelAlarm(context)
        onDismiss()
    }

    fun onSkipTomorrow() {
        val time : Calendar = setAlarmTime
        Log.d("AlarmCancellationSheet", "Skipping | time: ${time.toReadable()}")
        time.add(Calendar.HOUR_OF_DAY, 24)
        Log.d("AlarmCancellationSheet", "Skipping | time: ${time.toReadable()}")
        AppAlarmManager.scheduleSleepAlarm(context, time, isDaily = true)
        context.toast("Scheduled time: ${time.toReadable()}")
        onDismiss()
    }

    fun onDeleteDaily() {
        AppPreferences.dailyAlarm = null
        onDismiss()
    }

    fun onCancelOnceOffAndRevertToDaily() {
        require(dailyAlarm != null, { "Can't revert to daily, daily alarm value is null" })
        setAlarmTime.applyLocalTime(dailyAlarm)
        AppAlarmManager.scheduleSleepAlarm(context, setAlarmTime, isDaily = false)
        context.toast("Scheduled time: ${setAlarmTime.toReadable()}")
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .padding(bottom = bottomPadding)
        ) {
            Text(
                text = "Cancel Alarm",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            if (dailyAlarm == null) { // Case A: Only a single, one-off alarm
                RListItem(
                    headlineContent = { Text("Cancel This Alarm", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("The upcoming alarm will be removed.") },
                    modifier = Modifier.clickable(onClick = { onCancelOnceOff() })
                )
            }
            else if (dailyAlarm == setAlarmTime.toLocalTime()) { // Case B: Daily alarm is set
                Log.d("AlarmCancelSheet", "${dailyAlarm} : ${setAlarmTime.toLocalTime()}")
                RListItem(
                    headlineContent = { Text("Skip Tomorrow's Alarm", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Your daily schedule will resume the day after.") },
                    modifier = Modifier.clickable(onClick = { onSkipTomorrow() })
                )
                RListItem(
                    headlineContent = { Text("Turn Off Daily Alarm", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("This will permanently delete the repeating alarm.") },
                    modifier = Modifier.clickable(onClick = { onDeleteDaily() })
                )
            } else if (dailyAlarm != setAlarmTime.toLocalTime()) { // Case C: Daily alarm with a one-off override for tomorrow
                Log.d("AlarmCancelSheet", "${dailyAlarm} : ${setAlarmTime.toLocalTime()}")
                RListItem(
                    headlineContent = { Text("Use Daily Time Instead", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Reverts to your normally scheduled alarm time.") },
                    modifier = Modifier.clickable(onClick = { onCancelOnceOffAndRevertToDaily() })
                )
                RListItem(
                    headlineContent = { Text("Skip Tomorrow's Alarm", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("No alarm will ring tomorrow at all.") },
                    modifier = Modifier.clickable(onClick = { onSkipTomorrow() })
                )
                RListItem(
                    headlineContent = { Text("Turn Off Daily Alarm", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Deletes the custom time and the entire daily schedule.") },
                    modifier = Modifier.clickable(onClick = { onDeleteDaily() })
                )
            }
        }
    }
}