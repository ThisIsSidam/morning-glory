package app.morning.glory.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.morning.glory.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmUpdateSheet(
    isDailyAlarmSet: Boolean, // New parameter to control the text
    onDismissRequest: () -> Unit,
    onOneTimeAlarmClick: () -> Unit,
    onDailyAlarmClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val title: String
    val oneTimeHeadline: String
    val oneTimeSupportingText: String
    val dailyHeadline: String
    val dailySupportingText: String

    if (isDailyAlarmSet) {
        title = "Update This Alarm?"
        oneTimeHeadline = "Just for once"
        oneTimeSupportingText = "Your daily schedule won't be changed."
        dailyHeadline = "Update Daily Schedule"
        dailySupportingText = "This will be the new time every day."
    } else {
        title = "Set an Alarm"
        oneTimeHeadline = "Set One-Time Alarm"
        oneTimeSupportingText = "Triggers only once"
        dailyHeadline = "Set as Daily Alarm"
        dailySupportingText = "Triggers at this time every day."
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .padding(bottom = bottomPadding)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            RListItem(
                headlineContent = {
                    Text(oneTimeHeadline, fontWeight = FontWeight.SemiBold)
                },
                supportingContent = { Text(oneTimeSupportingText) },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.outline_today_24),
                        contentDescription = "One-time alarm"
                    )
                },
                modifier = Modifier.clickable(onClick = onOneTimeAlarmClick)
            )

            RListItem(
                headlineContent = {
                    Text(dailyHeadline, fontWeight = FontWeight.SemiBold)
                },
                supportingContent = { Text(dailySupportingText) },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.outline_event_repeat_24),
                        contentDescription = "Daily alarm"
                    )
                },
                modifier = Modifier.clickable(onClick = onDailyAlarmClick)
            )
        }
    }
}