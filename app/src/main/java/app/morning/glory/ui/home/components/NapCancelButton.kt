package app.morning.glory.ui.home.components

import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences

@Composable
fun NapCancelButton() {

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

    AnimatedVisibility(
        visible = napTime != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Button(
            onClick = {
                AppAlarmManager.cancelAlarm(context, AlarmType.NAP)
                context.toast("Cancelled Alarm")
            },
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) { Text("Cancel") }
    }

}