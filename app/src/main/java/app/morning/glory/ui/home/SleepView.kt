package app.morning.glory.ui.home

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.toLocalTime
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.shared.components.RadioButtonGroup
import app.morning.glory.ui.home.components.ButtonSection
import app.morning.glory.ui.home.components.DurationPicker
import app.morning.glory.ui.home.components.SleepHeader
import app.morning.glory.ui.home.components.TimePicker
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SleepView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val initialTime = AppPreferences.sleepAlarmTime
    var selectedTime by remember {
        mutableStateOf(initialTime ?: Calendar.getInstance().apply {
            val lastUsed = AppPreferences.lastUsedSleepTime
            set(Calendar.HOUR_OF_DAY, lastUsed.hour)
            set(Calendar.MINUTE, lastUsed.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        })
    }
    val is24HourFormat by remember { mutableStateOf(DateFormat.is24HourFormat(context)) }
    var showTimePicker by remember { mutableStateOf(true) }

    val onTimeSelected: (Calendar) -> Unit = { time ->
        selectedTime = time
        AppPreferences.lastUsedSleepTime = time.toLocalTime()
    }

    val onDurationSelected: (Long) -> Unit = { durationMs ->
        selectedTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis() + durationMs
        }
        AppPreferences.lastUsedSleepInDuration = durationMs.milliseconds
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(2f))

        SleepHeader()

        Spacer(modifier = Modifier.weight(1f))

        // Main Content Row
        Row(
            modifier = Modifier
                .padding(16.dp),
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

            // Picker in the center
            Box(
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = showTimePicker,
                    transitionSpec = {
                        (slideInHorizontally { width -> width } + fadeIn())
                            .togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                            .using(
                                SizeTransform(clip = false)
                            )
                    },
                    label = "PickerAnimation"
                ) { isTimePicker ->
                    if (isTimePicker) {
                        TimePicker(
                            initialTime = selectedTime,
                            is24HourFormat = is24HourFormat,
                            onTimeSelected = onTimeSelected
                        )
                    } else {
                        DurationPicker(
                            initDuration = AppPreferences.lastUsedSleepInDuration.inWholeMinutes.toInt(),
                            onDurationSelected = onDurationSelected
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ButtonSection(selectedTime)

        Spacer(modifier = Modifier.weight(1f))
    }
}
