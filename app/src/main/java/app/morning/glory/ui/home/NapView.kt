package app.morning.glory.ui.home

import android.content.SharedPreferences
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import app.morning.glory.R
import app.morning.glory.core.extensions.friendly
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.home.components.NapDurationGrid
import app.morning.glory.ui.home.components.NapHeader
import app.morning.glory.ui.home.components.sheets.NapDurationsSheet
import java.util.Calendar
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NapView(
    modifier: Modifier = Modifier
) {
    var dur1: Duration by remember { mutableStateOf(AppPreferences.napDuration1) }
    var dur2: Duration by remember { mutableStateOf(AppPreferences.napDuration2) }
    var dur3: Duration by remember { mutableStateOf(AppPreferences.napDuration3) }
    var dur4: Duration by remember { mutableStateOf(AppPreferences.napDuration4) }

    var showEditSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                AppPreferences.NAP_DURATION_1 -> dur1 = AppPreferences.napDuration1
                AppPreferences.NAP_DURATION_2 -> dur2 = AppPreferences.napDuration2
                AppPreferences.NAP_DURATION_3 -> dur3 = AppPreferences.napDuration3
                AppPreferences.NAP_DURATION_4 -> dur4 = AppPreferences.napDuration4
            }
        }
        AppPreferences.registerListener(listener)
        onDispose { AppPreferences.unregisterListener(listener) }
    }

    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState = sheetState
        ) {
            NapDurationsSheet(onDismiss = { showEditSheet = false })
        }
    }

    val context = LocalContext.current
    val scheduleAlarm = { duration: Duration ->
        val time = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis() + duration.inWholeMilliseconds
        }
        AppAlarmManager.scheduleAlarm(context, time, AlarmType.NAP)
        context.toast("Scheduled time: ${time.friendly(context)}")
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(2f))
        NapHeader()
        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { showEditSheet = true },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .align(Alignment.End)
                .size(32.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.round_edit_24),
                contentDescription = "Edit durations",
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .fillMaxWidth()
                .animateContentSize()
                .clip(RoundedCornerShape(24.dp))
        ) {
            NapDurationGrid(
                durations = listOf(dur1, dur2, dur3, dur4),
                onTap = { index ->
                    val selectedDuration = when (index) {
                        0 -> dur1
                        1 -> dur2
                        2 -> dur3
                        else -> dur4
                    }
                    scheduleAlarm(selectedDuration)
                }
            )

            NapCancelButton(Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.weight(2f))
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
        onDispose { AppPreferences.unregisterListener(listener) }
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
