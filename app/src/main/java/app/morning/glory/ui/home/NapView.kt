package app.morning.glory.ui.home

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.friendly
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.home.components.NapHeader
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun NapView(
    modifier: Modifier = Modifier
) {
    AppPreferences.napAlarmTime

    /// The gap between buttons
    val gap = 4.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(2f))
        NapHeader()
        Spacer(modifier = Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.spacedBy(gap),
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier.fillMaxWidth()
            ) {
                NapAlarmButton(duration = 1.minutes, modifier.weight(1f))
                NapAlarmButton(duration = 20.minutes, modifier.weight(1f))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier.fillMaxWidth()
            ) {
                NapAlarmButton(duration = 30.minutes, modifier.weight(1f))
                NapAlarmButton(duration = 45.minutes, modifier.weight(1f))
            }

            NapCancelButton(Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun NapAlarmButton(duration: Duration, modifier: Modifier) {
    val context = LocalContext.current
    Button(
        onClick = {
            // Schedule time = Current Time + Duration
            val time = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis() + duration.inWholeMilliseconds
            }

            // Schedule alarm and notify with toast
            AppAlarmManager.scheduleAlarm(context, time, AlarmType.NAP)
            context.toast("Scheduled time: ${time.friendly(context)}")
        },
        modifier = modifier.height(80.dp),
        shape = RectangleShape
    ) {
        Text(duration.friendly())
    }
}

@Composable
fun NapCancelButton(modifier: Modifier) {

    val context = LocalContext.current
    var napTime by remember { mutableStateOf(AppPreferences.napAlarmTime) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.NAP_TIME) {
                napTime = AppPreferences.napAlarmTime
            }
        }

        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    if (napTime == null) return

    Button(
        onClick = {
            AppAlarmManager.cancelAlarm(context, AlarmType.NAP)
            context.toast("Cancelled Alarm")
        },
        modifier = modifier.height(80.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text("Cancel")
    }
}