package app.morning.glory.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.morning.glory.shared.components.Picker
import app.morning.glory.shared.components.rememberPickerState


@Composable
fun DurationPicker(
    /// [initDuration] is duration count in minutes, should be >= 0 and < 24 hours
    initDuration: Long = 0,
    onDurationSelected: (Long) -> Unit
) {
    require(initDuration >= 0 && initDuration < 24 * 60) { "initDuration should be >= 0 and less than 24 hours"}

    var hourPickerState = rememberPickerState()
    var minutePickerState = rememberPickerState()

    LaunchedEffect(hourPickerState.selectedItem, minutePickerState.selectedItem) {
        val hours = hourPickerState.selectedItem.toIntOrNull() ?: return@LaunchedEffect
        val minutes = minutePickerState.selectedItem.toIntOrNull() ?: return@LaunchedEffect
        val durationMs = (hours * 60 * 60 * 1000L) + (minutes * 60 * 1000L)
        onDurationSelected(durationMs)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Hours Picker
        Picker(
            items = List(23) { it.toString() },
            startIndex = (initDuration / 60).toInt(),
            state = hourPickerState,
            visibleItemsCount = 7,
        )
        Text(
            text = "Hr",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Minutes Picker
        Picker(
            items = List(60) { it.toString() },
            startIndex = (initDuration % 60).toInt(),
            state = minutePickerState,
            visibleItemsCount = 7,
        )
        Text(
            text = "Mn",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
