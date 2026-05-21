package app.morning.glory.ui.home

import android.app.Activity
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import app.morning.glory.ui.theme.AppFontFamily
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun NightClockView(onBack: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }
    val is24HourFormat = DateFormat.is24HourFormat(context)

    // Dimming state (0f = bright, 0.95f = very dim)
    var dimAlpha by remember { mutableFloatStateOf(0f) }

    // Immersive Mode: Hide Status and Nav bars
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        val windowInsetsController = WindowCompat.getInsetsController(window, view)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance()
            delay(1000)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onBack() })
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        // Sensitivity: sliding down (positive dragAmount) increases dimAlpha
                        val delta = dragAmount / 1000f
                        dimAlpha = (dimAlpha + delta).coerceIn(0f, 0.95f)
                        change.consume()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = this@BoxWithConstraints.maxWidth > this@BoxWithConstraints.maxHeight

        // Dynamic scaling based on available dimensions
        // - Base portrait: 360dp width
        // - Base landscape: 450dp width
        // - We cap the scale at 2.5x to prevent overflow on giant tablets/desktops
        val scaleFactor = if (isLandscape) {
            (maxWidth.value / 450f).coerceIn(1.5f, 2.5f)
        } else {
            (maxWidth.value / 420f).coerceIn(1.0f, 2.0f)
        }

        // Center Clock and Day
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
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
                    fontSize = (100 * scaleFactor).sp,
                    fontFamily = AppFontFamily.Orbitron,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // AM/PM and Seconds
                Column(
                    modifier = Modifier.padding(
                        bottom = (24 * scaleFactor).dp,
                        start = (4 * scaleFactor).dp
                    ),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (!is24HourFormat) {
                        val amPm =
                            if (currentTime.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
                        Text(
                            text = amPm,
                            fontSize = (20 * scaleFactor).sp,
                            fontFamily = AppFontFamily.Orbitron,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    val seconds =
                        String.format(Locale.getDefault(), "%02d", currentTime.get(Calendar.SECOND))
                    Text(
                        text = seconds,
                        fontSize = (30 * scaleFactor).sp,
                        fontFamily = AppFontFamily.Orbitron,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }

            // Day of Week - Bound tightly to the time digits
            val dayOfWeek =
                SimpleDateFormat("EEEE", Locale.getDefault()).format(currentTime.time).uppercase()
            Text(
                text = dayOfWeek,
                fontSize = (32 * scaleFactor).sp,
                fontFamily = AppFontFamily.Orbitron,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(y = ((-24) * scaleFactor).dp),
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
