package app.morning.glory.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun HomeScreen(
    onSave: (hour: Int, minute: Int) -> Unit = { _, _ -> }
) {
    var time by remember { mutableStateOf(Calendar.getInstance()) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { showDialog = true }) {
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        time.get(Calendar.HOUR_OF_DAY),
                        time.get(Calendar.MINUTE)
                    )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { onSave(time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)) }) {
                Text("Save")
            }
        }
        if (showDialog) {
            TimePickerDialog(
                initialHour = time.get(Calendar.HOUR_OF_DAY),
                initialMinute = time.get(Calendar.MINUTE),
                onTimeSelected = { hour, minute ->
                    time.set(Calendar.HOUR_OF_DAY, hour)
                    time.set(Calendar.MINUTE, minute)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                NumberPicker(
                    value = hour,
                    range = 0..23,
                    onValueChange = { hour = it }
                )
                Text(" : ")
                NumberPicker(
                    value = minute,
                    range = 0..59,
                    onValueChange = { minute = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onTimeSelected(hour, minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
            Text("-")
        }
        Text(
            text = value.toString().padStart(2, '0'),
            modifier = Modifier.width(32.dp)
        )
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
            Text("+")
        }
    }
}