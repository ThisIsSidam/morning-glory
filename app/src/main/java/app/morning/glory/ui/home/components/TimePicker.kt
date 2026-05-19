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
    var isAm by remember { mutableStateOf(initialTime.get(Calendar.AM_PM) == Calendar.AM) }
    val hourPickerState = rememberPickerState()
    val minutePickerState = rememberPickerState()

    LaunchedEffect(hourPickerState.selectedItem, minutePickerState.selectedItem, isAm, is24HourFormat) {
        val hour = hourPickerState.selectedItem.toIntOrNull() ?: return@LaunchedEffect
        val minute = minutePickerState.selectedItem.toIntOrNull() ?: return@LaunchedEffect

        val newTime = Calendar.getInstance().apply {
            if (is24HourFormat) {
                set(Calendar.HOUR_OF_DAY, hour)
            } else {
                set(Calendar.HOUR, if (hour == 12) 0 else hour)
                set(Calendar.AM_PM, if (isAm) Calendar.AM else Calendar.PM)
            }
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (newTime.isInPast()) newTime.add(Calendar.HOUR_OF_DAY, 24)
        onTimeSelected(newTime)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Hours
        Picker(
            items = if (is24HourFormat) List(24) { it.toString() } else List(12) { (it + 1).toString() },
            startIndex = if (is24HourFormat) {
                initialTime.get(Calendar.HOUR_OF_DAY)
            } else {
                initialTime.get(Calendar.HOUR).let { if (it == 0) 11 else it - 1 }
            },
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
            items = List(60) { it.toString().padStart(2, '0') },
            startIndex = initialTime.get(Calendar.MINUTE),
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