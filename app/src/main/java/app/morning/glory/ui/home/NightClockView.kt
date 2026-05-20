package app.morning.glory.ui.home

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun NightClockView(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }
    val is24HourFormat = DateFormat.is24HourFormat(context)

    // Dimming state (0f = bright, 0.95f = very dim)
    var dimAlpha by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance()
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures {
                    onBack()
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    // Sensitivity: sliding down (positive dragAmount) increases dimAlpha
                    val delta = dragAmount / 1000f
                    dimAlpha = (dimAlpha + delta).coerceIn(0f, 0.95f)
                    change.consume()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Center Clock and Day
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                // Hour and Minutes
                val timeFormat = if (is24HourFormat) "HH:mm" else "h:mm"
                val timeText =
                    SimpleDateFormat(timeFormat, Locale.getDefault()).format(currentTime.time)

                Text(
                    text = timeText,
                    fontSize = 100.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // AM/PM and Seconds
                Column(
                    modifier = Modifier.padding(bottom = 24.dp, start = 4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (!is24HourFormat) {
                        val amPm =
                            if (currentTime.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
                        Text(
                            text = amPm,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    val seconds =
                        String.format(Locale.getDefault(), "%02d", currentTime.get(Calendar.SECOND))
                    Text(
                        text = seconds,
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }

            // Day of Week
            val dayOfWeek =
                SimpleDateFormat("EEE", Locale.getDefault()).format(currentTime.time).uppercase()
            Text(
                text = dayOfWeek,
                fontSize = 32.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        // Dimming Overlay (placed last to cover everything)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = dimAlpha))
        )
    }
}
