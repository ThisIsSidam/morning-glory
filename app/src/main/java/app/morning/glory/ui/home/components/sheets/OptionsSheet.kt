package app.morning.glory.ui.home.components.sheets

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.morning.glory.core.utils.AppPreferences

@Composable
fun OptionsSheet() {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .padding(bottom = bottomPadding)
    ) {
        Text(
            text = "Options",
            style = MaterialTheme.typography.headlineSmall,
        )
        SnoozeOptionTile()
        SnoozeDurationTile()
        WakeCheckAlarmTimeTile()
    }
}

@Composable
fun SnoozeOptionTile() {
    var showPopup by remember { mutableStateOf(false) }
    var selectedSnoozeLimit by remember { mutableIntStateOf(AppPreferences.maxSnoozeCount) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.MAX_SNOOZE_COUNT_KEY) {
                selectedSnoozeLimit = AppPreferences.maxSnoozeCount
            }
        }
        AppPreferences.registerListener(listener)
        onDispose { AppPreferences.unregisterListener(listener) }
    }

    OptionsTile(
        title = "Snooze limit",
        description = "The max allowed number of snoozes",
        valueText = selectedSnoozeLimit.toString(),
        onClick = { showPopup = true },
        trailingContent = {
            if (showPopup) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { showPopup = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    (1..4).forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.toString(),
                                    color = if (option == selectedSnoozeLimit)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                AppPreferences.maxSnoozeCount = option
                                showPopup = false
                            },
                            modifier = if (option == selectedSnoozeLimit)
                                Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            else Modifier
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SnoozeDurationTile() {
    var showPopup by remember { mutableStateOf(false) }
    var selectedSnoozeDur by remember { mutableIntStateOf(AppPreferences.snoozeDurationMinutes) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.SNOOZE_DURATION_KEY) {
                selectedSnoozeDur = AppPreferences.snoozeDurationMinutes
            }
        }
        AppPreferences.registerListener(listener)
        onDispose { AppPreferences.unregisterListener(listener) }
    }

    OptionsTile(
        title = "Snooze duration",
        description = "The duration of each snooze in minutes",
        valueText = "$selectedSnoozeDur min",
        onClick = { showPopup = true },
        trailingContent = {
            if (showPopup) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { showPopup = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    listOf(5, 10, 15, 30).forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "$option min",
                                    color = if (option == selectedSnoozeDur)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                AppPreferences.snoozeDurationMinutes = option
                                showPopup = false
                            },
                            modifier = if (option == selectedSnoozeDur)
                                Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            else Modifier
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun WakeCheckAlarmTimeTile() {
    var showPopup by remember { mutableStateOf(false) }
    var wakeCheckTime by remember { mutableIntStateOf(AppPreferences.wakeCheckAlarmTime) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.WAKE_CHECK_ALARM_TIME_KEY) {
                wakeCheckTime = AppPreferences.wakeCheckAlarmTime
            }
        }
        AppPreferences.registerListener(listener)
        onDispose { AppPreferences.unregisterListener(listener) }
    }

    OptionsTile(
        title = "Wake check time",
        description = "How early the wake check notification should be sent",
        valueText = "$wakeCheckTime min",
        onClick = { showPopup = true },
        trailingContent = {
            if (showPopup) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { showPopup = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    listOf(15, 30, 60).forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "$option min",
                                    color = if (option == wakeCheckTime)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                AppPreferences.wakeCheckAlarmTime = option
                                showPopup = false
                            },
                            modifier = if (option == wakeCheckTime)
                                Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            else Modifier
                        )
                    }
                }
            }
        }
    )
}
