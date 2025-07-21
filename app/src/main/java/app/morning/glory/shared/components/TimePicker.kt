package app.morning.glory.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun TimePicker(
    initialTime: Calendar,
    is24HourFormat: Boolean,
    onTimeSelected: (Calendar) -> Unit
) {
    var time by remember { mutableStateOf(initialTime) }
    var isAm by remember { mutableStateOf(time.get(Calendar.AM_PM) == Calendar.AM) }

    LaunchedEffect(time) {
        onTimeSelected(time)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Hours
        NumberPicker(
            valueMax = if (is24HourFormat) 23 else 12,
            valueMin = if (is24HourFormat) 0 else 1,
            currentValue = if (is24HourFormat) time.get(Calendar.HOUR_OF_DAY)
            else time.get(Calendar.HOUR).let { if (it == 0) 12 else it },
            onValueChange = { hour ->
                val newTime = time.clone() as Calendar
                newTime.set(Calendar.HOUR_OF_DAY, if (is24HourFormat) hour
                else if (isAm) if (hour == 12) 0 else hour
                else if (hour == 12) 12 else hour + 12)
                time = newTime
            },
        )

        Text(
            ":",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Minutes
        NumberPicker(
            valueMax = 59,
            valueMin = 0,
            currentValue = time.get(Calendar.MINUTE),
            onValueChange = { minute ->
                val newTime = time.clone() as Calendar
                newTime.set(Calendar.MINUTE, minute)
                time = newTime
            },
        )

        // AM/PM Toggle (only for 12-hour format)
        if (!is24HourFormat) {
            Spacer(modifier = Modifier.width(16.dp))

            RadioButtonGroup(
                options = listOf("AM", "PM"),
                selectedOption = if (isAm) "AM" else "PM",
                onSelectionChanged = { option ->
                    isAm = option.trim() == "AM"
                }
            )

        }
    }
}