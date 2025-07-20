package app.morning.glory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DurationPicker(
    initialDuration: Long = 0,
    onDurationSelected: (Long) -> Unit
) {
    var hours by remember { mutableIntStateOf((initialDuration / (60 * 60 * 1000)).toInt()) }
    var minutes by remember { mutableIntStateOf(((initialDuration % (60 * 60 * 1000)) / (60 * 1000)).toInt()) }

    LaunchedEffect(hours, minutes) {
        val durationMs = (hours * 60 * 60 * 1000L) + (minutes * 60 * 1000L)
        onDurationSelected(durationMs)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Hours Picker
        NumberPicker(
            valueMax = 23,
            valueMin = 0,
            currentValue = hours,
            onValueChange = { hours = it },
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
        NumberPicker(
            valueMax = 59,
            valueMin = 0,
            currentValue = minutes,
            onValueChange = { minutes = it },
        )
        Text(
            text = "Mn",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
