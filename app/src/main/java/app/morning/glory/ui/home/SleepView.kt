package app.morning.glory.ui.home

import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.shared.components.RadioButtonGroup
import app.morning.glory.ui.home.components.DurationPicker
import app.morning.glory.ui.home.components.SleepHeader
import app.morning.glory.ui.home.components.TimePicker
import app.morning.glory.ui.home.components.ButtonSection
import java.util.Calendar

@Composable
fun SleepView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var selectedTime by remember { mutableStateOf(AppPreferences.sleepAlarmTime ?: Calendar.getInstance()) }
    var is24HourFormat by remember { mutableStateOf(DateFormat.is24HourFormat(context)) }
    var showTimePicker by remember { mutableStateOf(true) }

    val onTimeSelected: (Calendar) -> Unit = { selectedTime = it }
    val onDurationSelected: (Long) -> Unit = { durationMins ->
        selectedTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis() + durationMins
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(2f))

        SleepHeader()

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.padding(16.dp),
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

            Spacer(modifier = Modifier.width(24.dp))

            Box(contentAlignment = Alignment.Center) {
                if (showTimePicker) {
                    TimePicker(
                        initialTime = selectedTime,
                        is24HourFormat = is24HourFormat,
                        onTimeSelected = onTimeSelected
                    )
                } else {
                    DurationPicker(
                        initDuration = 7 * 60 + 30,
                        onDurationSelected = onDurationSelected
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ButtonSection(selectedTime)

        Spacer(modifier = Modifier.weight(1f))
    }
}
