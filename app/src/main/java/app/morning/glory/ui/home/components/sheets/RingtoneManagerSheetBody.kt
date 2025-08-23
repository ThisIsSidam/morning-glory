package app.morning.glory.ui.home.components.sheets

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.morning.glory.R
import app.morning.glory.core.audio.RingtoneInfo
import app.morning.glory.ui.home.viewmodels.RingtoneViewModel

val LocalRingtoneViewModel = compositionLocalOf<RingtoneViewModel> {
    error("No NameViewModel provided")
}

@Composable
fun RingtoneManagerSheetBody(viewModel: RingtoneViewModel = viewModel()) {
    CompositionLocalProvider(LocalRingtoneViewModel provides viewModel) {
        RingtoneManagerSheetContent()
    }
}

@Composable
fun RingtoneManagerSheetContent() {
    val viewModel = LocalRingtoneViewModel.current
    val uiState by viewModel.uiState.collectAsState()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .padding(bottom = bottomPadding)
    ) {
        Text(
            text = "Ringtones",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
            item {
                RingtoneListItem(
                    isRemovable = false,
                    ringtoneInfo = uiState.defaultRingtone,
                )
            }

            items(uiState.savedRingtones, key = { it.uri }) { ringtoneInfo ->
                RingtoneListItem(ringtoneInfo = ringtoneInfo)
            }
        }

        if (uiState.savedRingtones.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Swipe to remove a ringtone",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
        }

        AddRingtoneButton { viewModel.addRingtone(it) }

        RingtoneRandomizeTile(
            checked = uiState.randomizeTones,
            onCheckedChange = { viewModel.setRandomizeTones(it) }
        )
    }
}

@Composable
fun RingtoneListItem(
    isRemovable: Boolean = true,
    ringtoneInfo: RingtoneInfo,
) {
    val viewModel = LocalRingtoneViewModel.current
    val uiState by viewModel.uiState.collectAsState()
    val isPlaying = uiState.playingUri == ringtoneInfo.uri
    val isSelected = !uiState.randomizeTones && uiState.selectedRingtoneUri == ringtoneInfo.uri

    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (!isRemovable) return@rememberSwipeToDismissBoxState false
            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                viewModel.removeRingtone(ringtoneInfo)
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(state = state, backgroundContent = { /* ... */ }) {
        ListItem(
            modifier = Modifier.clickable { viewModel.selectRingtone(ringtoneInfo.uri) },
            headlineContent = { Text(text = ringtoneInfo.name, style = MaterialTheme.typography.bodyLarge) },
            supportingContent = {
                if (isSelected) {
                    Text("Selected", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                } else {
                    Text("Tap to select", style = MaterialTheme.typography.bodySmall)
                }
            },
            trailingContent = {
                IconButton(onClick = { viewModel.togglePlayPause(ringtoneInfo.uri) }) {
                    Icon(
                        painter = painterResource(if (isPlaying) R.drawable.round_stop_24 else R.drawable.round_play_arrow_24),
                        contentDescription = if (isPlaying) "Stop" else "Play"
                    )
                }
            },
        )
    }
}

@Composable
fun AddRingtoneButton(onAdd: (Uri) -> Unit) {

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentData: Intent? = result.data

            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intentData?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intentData?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            // Notify the ViewModel of the event
            uri?.let { onAdd(it) }
        }
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound")
                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
            }
            ringtonePickerLauncher.launch(intent)
        }
    ) {
        Text(modifier = Modifier.padding(vertical = 4.dp), text = "Add Ringtone")
    }
}

// This component is now fully stateless and reusable.
@Composable
fun RingtoneRandomizeTile(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Randomize Ringtones",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Ringtones are randomly selected for each alarm",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}