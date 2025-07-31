package app.morning.glory.ui.home.components

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.toReadable
import app.morning.glory.core.utils.AppPreferences

@Composable
fun NapHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nap",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        NextNapAlarmText()
    }
}

@Composable
fun NextNapAlarmText() {
    var nextAlarmTime = remember {mutableStateOf(AppPreferences.sleepAlarmTime)}

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.NAP_TIME) {
                nextAlarmTime.value = AppPreferences.sleepAlarmTime
            }
        }

        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    Text(
        text = if (nextAlarmTime.value == null) "No alarm set!" else "Next alarm at ${nextAlarmTime.value!!.toReadable()}"
    )
}