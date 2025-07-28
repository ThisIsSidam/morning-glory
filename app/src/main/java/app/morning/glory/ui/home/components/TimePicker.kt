package app.morning.glory.ui.home.components

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
import app.morning.glory.shared.components.Picker
import app.morning.glory.shared.components.RadioButtonGroup
import app.morning.glory.shared.components.rememberPickerState
import java.util.Calendar

@Composable
fun TimePicker(
    initialTime: Calendar,
    is24HourFormat: Boolean,
    onTimeSelected: (Calendar) -> Unit
) {
    var time by remember { mutableStateOf(initialTime) }
    var isAm by remember { mutableStateOf(time.get(Calendar.AM_PM) == Calendar.AM) }
    var hourPickerState = rememberPickerState()
    var minutePickerState = rememberPickerState()

    LaunchedEffect(hourPickerState.selectedItem, minutePickerState.selectedItem) {
        val hour = hourPickerState.selectedItem.toIntOrNull() ?: return@LaunchedEffect
        val minute = minutePickerState.selectedItem.toIntOrNull() ?: return@LaunchedEffect

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.AM_PM, if (isAm) Calendar.AM else Calendar.PM)
        }

        onTimeSelected(calendar)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Hours
        Picker(
            items = if (is24HourFormat) List(24) { it.toString() } else List(12) { it.toString() },
            startIndex = if (is24HourFormat) time.get(Calendar.HOUR_OF_DAY)
            else time.get(Calendar.HOUR).let { if (it == 0) 12 else it },

            visibleItemsCount = 7,
            state = hourPickerState
//            onValueChange = { hour ->
//                val newTime = time.clone() as Calendar
//                newTime.set(Calendar.HOUR_OF_DAY, if (is24HourFormat) hour
//                else if (isAm) if (hour == 12) 0 else hour
//                else if (hour == 12) 12 else hour + 12)
//                time = newTime
//            },
        )

        Text(
            ":",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Minutes
        Picker(
            items = List(60) { it.toString() },
            startIndex = time.get(Calendar.MINUTE),
            visibleItemsCount = 7,
            state = minutePickerState
//            onValueChange = { minute ->
//                val newTime = time.clone() as Calendar
//                newTime.set(Calendar.MINUTE, minute)
//                time = newTime
//            },
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