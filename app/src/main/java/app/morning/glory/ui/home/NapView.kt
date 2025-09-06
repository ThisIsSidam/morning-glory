package app.morning.glory.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.morning.glory.core.extensions.friendly
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppAlarmManager
import app.morning.glory.ui.home.components.DurationGrid
import app.morning.glory.ui.home.components.NapCancelButton
import app.morning.glory.ui.home.components.NapHeader
import java.util.Calendar

@Composable
fun NapView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        NapHeader()

        Spacer(modifier = Modifier.weight(1f))

        DurationGrid(
            modifier = Modifier
                .height(200.dp)
                .width(300.dp)
        ) {
            val scheduleTime = Calendar.getInstance()
            scheduleTime.add(Calendar.MINUTE, it)
            AppAlarmManager.scheduleAlarm(context, scheduleTime, AlarmType.NAP)
            context.toast("Scheduled time: ${scheduleTime.friendly(context)}")
        }

        Spacer(modifier = Modifier.weight(1f))

        NapCancelButton()

        Spacer(modifier = Modifier.weight(1f))
    }
}
