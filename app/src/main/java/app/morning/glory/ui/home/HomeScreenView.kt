package app.morning.glory.ui.home

import android.text.format.DateFormat
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.morning.glory.shared.components.DurationPicker
import app.morning.glory.shared.components.RadioButtonGroup
import app.morning.glory.shared.components.TimePicker
import app.morning.glory.utils.AlarmHelper
import java.util.Calendar

@Composable
fun HomeScreenView(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    var selectedTime by remember { mutableStateOf(Calendar.getInstance()) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var is24HourFormat by remember { mutableStateOf(DateFormat.is24HourFormat(context)) }
    var showTimePicker by remember { mutableStateOf(true) }

    val onTimeSelected: (Calendar) -> Unit = { time ->
        selectedTime = time
    }

    val onDurationSelected: (Long) -> Unit = { duration ->
        durationMs = duration
    }

    Column {

        // Main Content Row
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            RadioButtonGroup(
                options = listOf("At", "In"),
                selectedOption = if (showTimePicker) "At" else "In",
                onSelectionChanged = { option ->
                    showTimePicker = option.trim() == "At"
                }
            )

            // Picker in the center
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Log.d("HomeScreen", "showTimePicker: $showTimePicker, selectedTime: $selectedTime, durationMs: $durationMs")
                if (showTimePicker) {
                    TimePicker(
                        initialTime = selectedTime,
                        is24HourFormat = is24HourFormat,
                        onTimeSelected = onTimeSelected
                    )
                } else {
                    DurationPicker(
                        initialDuration = durationMs,
                        onDurationSelected = onDurationSelected
                    )
                }
            }
        }

        // Set Alarm Button
        Button(
            onClick = {
                val alarmTime = if (showTimePicker) {
                    selectedTime
                } else {
                    Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis() + durationMs
                    }
                }
                AlarmHelper.scheduleAlarm(context, alarmTime)
            },
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text("Set Alarm")
        }
    }
}



