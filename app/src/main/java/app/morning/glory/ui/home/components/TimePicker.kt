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
import app.morning.glory.core.extensions.isInPast
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

    LaunchedEffect(hourPickerState.selectedItem, minutePickerState.selectedItem, isAm) {
        val hour = hourPickerState.selectedItem.toIntOrNull() ?: return@LaunchedEffect
        val minute = minutePickerState.selectedItem.toIntOrNull() ?: return@LaunchedEffect

        val time = Calendar.getInstance().apply {
            set(Calendar.HOUR, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.AM_PM, if (isAm) Calendar.AM else Calendar.PM)
        }

        if (time.isInPast()) time.add(Calendar.HOUR_OF_DAY, 24)
        onTimeSelected(time)
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
        )

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