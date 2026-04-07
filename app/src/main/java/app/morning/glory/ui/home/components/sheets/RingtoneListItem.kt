package app.morning.glory.ui.home.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import app.morning.glory.R
import app.morning.glory.core.audio.RingtoneInfo
import app.morning.glory.core.utils.AppPreferences

@Composable
fun RingtoneListItem(
    ringtoneInfo: RingtoneInfo,
    isPlaying: Boolean,
    isSelected: Boolean,
    trailingAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (!isSelected) {
                    AppPreferences.selectedRingtone = ringtoneInfo.uri
                }
            },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
            headlineColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            supportingColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            }
        ),
        headlineContent = {
            Text(
                text = ringtoneInfo.name,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        supportingContent = {
            Text(
                text = if (isSelected) "Active ringtone" else "Tap to select",
                style = MaterialTheme.typography.bodySmall,
            )
        },
        trailingContent = {
            Button(onClick = trailingAction) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.round_stop_24 else R.drawable.round_play_arrow_24),
                    contentDescription = if (isPlaying) "Stop" else "Play"
                )
            }
        },
    )
}
