package app.morning.glory.ui.home.components

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.morning.glory.R
import app.morning.glory.core.audio.RingtoneHelper
import app.morning.glory.core.audio.RingtoneInfo


@Preview()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingtoneManagerSheetBody() {
    val context = LocalContext.current
    val selectedRingtones = remember { mutableStateListOf<RingtoneInfo>() }
    var playingUri by remember { mutableStateOf<Uri?>(null) }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentData: Intent? = result.data

            // --- THIS IS THE UPDATED SECTION ---

            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use the new, type-safe method for Android 13 and above
                intentData?.getParcelableExtra(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                    Uri::class.java
                )
            } else {
                // Use the old, deprecated method for older Android versions
                @Suppress("DEPRECATION")
                intentData?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }

            // --- END OF UPDATED SECTION ---


            uri?.let {
                val ringtone = RingtoneManager.getRingtone(context, it)
                val name = ringtone.getTitle(context)
                selectedRingtones.add(RingtoneInfo(name, it))
            }
        }
    }

    // This effect will run when the composable is removed from the screen,
    // ensuring any playing sound is stopped.
    DisposableEffect(Unit) {
        onDispose {
            RingtoneHelper.stopRingtone()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        if (selectedRingtones.isEmpty()) {
            Text(
                text = "No audios added!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(32.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(selectedRingtones){ ringtone ->
                    RingtoneListItem(
                        ringtoneInfo = ringtone,
                        isPlaying = ringtone.uri == playingUri,
                        onActionClick = {
                            if (ringtone.uri == playingUri) {
                                // If it's playing, stop it and clear the playing state.
                                RingtoneHelper.stopRingtone()
                                playingUri = null
                            } else {
                                // If not playing, start it and set the playing state.
                                RingtoneHelper.playRingtone(context, ringtone.uri)
                                playingUri = ringtone.uri
                            }
                        }
                    )
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            onClick = {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound")
                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
            }
            ringtonePickerLauncher.launch(intent)
        }) {
            Text(modifier = Modifier.padding(vertical = 4.dp), text = "Add Ringtone")
        }
    }
}

@Composable
fun RingtoneListItem(
    ringtoneInfo: RingtoneInfo,
    isPlaying: Boolean,
    onActionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = ringtoneInfo.name,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            trailingContent = {
                Button(onClick = onActionClick) {
                    Icon(
                        painter = painterResource(if (isPlaying) R.drawable.round_stop_24 else R.drawable.round_play_arrow_24),
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }
            },
        )
    }
}