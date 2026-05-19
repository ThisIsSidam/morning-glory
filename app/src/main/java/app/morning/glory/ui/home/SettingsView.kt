package app.morning.glory.ui.home

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.morning.glory.R
import app.morning.glory.core.extensions.isIgnoringBatteryOptimizations
import app.morning.glory.core.extensions.requestIgnoreBatteryOptimizations
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.home.components.sheets.OptionsTile
import app.morning.glory.ui.home.components.sheets.QRCodeManagerSheetBody
import app.morning.glory.ui.home.components.sheets.RingtoneManagerSheetBody

enum class SettingsSheet {
    QRSheet,
    RingtoneSheet,
    NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView() {
    val context = LocalContext.current
    val isInitiallyUnrestricted = remember { context.isIgnoringBatteryOptimizations() }
    var showSettingsSheet by remember { mutableStateOf(SettingsSheet.NONE) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showSettingsSheet != SettingsSheet.NONE) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = SettingsSheet.NONE },
            sheetState = sheetState,
        ) {
            when (showSettingsSheet) {
                SettingsSheet.QRSheet -> QRCodeManagerSheetBody()
                SettingsSheet.RingtoneSheet -> RingtoneManagerSheetBody()
                else -> {}
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // Battery tile is at top when attention is required..
        // If not, it is placed in the bottom.
        if (!isInitiallyUnrestricted) {
            BatteryOptimizationTile()
        }

        OptionsTile(
            title = "QR Code",
            description = "Manage QR codes for dismissing alarms",
            icon = Icons.Default.Lock,
            onClick = { showSettingsSheet = SettingsSheet.QRSheet }
        )
        OptionsTile(
            title = "Ringtones",
            description = "Manage your alarm ringtones",
            iconRes = R.drawable.outline_queue_music_24,
            onClick = { showSettingsSheet = SettingsSheet.RingtoneSheet }
        )
        SnoozeOptionTile()
        SnoozeDurationTile()
        WakeCheckAlarmTimeTile()

        if (isInitiallyUnrestricted) {
            BatteryOptimizationTile()
        }
    }
}

@Composable
fun BatteryOptimizationTile() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isIgnoringBatteryOptimizations by remember { mutableStateOf(context.isIgnoringBatteryOptimizations()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isIgnoringBatteryOptimizations = context.isIgnoringBatteryOptimizations()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    OptionsTile(
        title = "Battery Optimization",
        description = "Recommended for reliable alarm triggering",
        valueText = if (isIgnoringBatteryOptimizations) "Unrestricted" else "Allow",
        icon = Icons.Default.Info,
        onClick = {
            if (!isIgnoringBatteryOptimizations) {
                context.requestIgnoreBatteryOptimizations()
            } else {
                context.toast("No Action Needed")
            }
        }
    )
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
        iconRes = R.drawable.outline_snooze_24,
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
        iconRes = R.drawable.round_pause_24,
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
        iconRes = R.drawable.outline_today_24,
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
