package app.morning.glory.ui.home.components.sheets

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ListItem
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
    var selectedSnoozeLimit by remember { mutableStateOf(AppPreferences.maxSnoozeCount) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == AppPreferences.MAX_SNOOZE_COUNT_KEY) {
                selectedSnoozeLimit = AppPreferences.maxSnoozeCount
            }
        }
        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { showPopup = true }
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = "Snooze limit",
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            supportingContent = {
                Text(
                    text = "The max allowed number of snoozes",
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            trailingContent = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = selectedSnoozeLimit.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (showPopup) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { showPopup = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        (1..4).forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
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
            },
        )
    }
}

@Composable
fun SnoozeDurationTile() {
    var showPopup by remember { mutableStateOf(false) }
    var selectedSnoozeDur by remember { mutableStateOf(AppPreferences.snoozeDurationMinutes) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == AppPreferences.SNOOZE_DURATION_KEY) {
                selectedSnoozeDur = AppPreferences.snoozeDurationMinutes
            }
        }
        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { showPopup = true }
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = "Snooze duration",
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            supportingContent = {
                Text(
                    text = "The duration of each snooze in minutes",
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            trailingContent = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$selectedSnoozeDur min",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (showPopup) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { showPopup = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        listOf(5, 10, 15, 30).forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
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
            },
        )
    }
}

@Composable
fun WakeCheckAlarmTimeTile() {
    var showPopup by remember { mutableStateOf(false) }
    var wakeCheckTime by remember { mutableStateOf(AppPreferences.wakeCheckAlarmTime) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == AppPreferences.WAKE_CHECK_ALARM_TIME_KEY) {
                wakeCheckTime = AppPreferences.wakeCheckAlarmTime
            }
        }
        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { showPopup = true }
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = "Wake check time",
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            supportingContent = {
                Text(
                    text = "How early the wake check notification should be sent",
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            trailingContent = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$wakeCheckTime min",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (showPopup) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { showPopup = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        listOf(15, 30, 60).forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
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
            },
        )
    }
}

