package app.morning.glory.ui.home.components.sheets

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.unit.dp
import app.morning.glory.R
import app.morning.glory.core.audio.AppSoundPlayer
import app.morning.glory.core.audio.RingtoneInfo
import app.morning.glory.core.utils.AppPreferences


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingtoneManagerSheetBody() {

    val context = LocalContext.current
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val player = AppSoundPlayer(context)
    val defInfo = RingtoneInfo(
        name = "Acoustic Guitar (Default)",
        uri = AppSoundPlayer.getDefaultRingtoneUri(context)
    )
    val savedRingtones = remember { mutableStateListOf<RingtoneInfo>(*AppPreferences.getRingtoneList().toTypedArray()) }
    val selectedRingtone = remember { mutableStateOf<Uri>(AppPreferences.selectedRingtone ?: defInfo.uri) }
    var playingUri by remember { mutableStateOf<Uri?>(null) }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentData: Intent? = result.data

            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intentData?.getParcelableExtra(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                    Uri::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intentData?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }

            uri?.let {
                val ringtone = RingtoneManager.getRingtone(context, it)
                val name = ringtone.getTitle(context)

                AppPreferences.addRingtone(RingtoneInfo(name, it))
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.RINGTONE_LIST_KEY) {
                savedRingtones.clear()
                savedRingtones.addAll(AppPreferences.getRingtoneList())
            } else if (key == AppPreferences.SELECTED_RINGTONE_KEY) {
                selectedRingtone.value = AppPreferences.selectedRingtone ?: defInfo.uri
            }
        }

        AppPreferences.registerListener(listener)
        onDispose {
            player.release()
            AppPreferences.unregisterListener(listener)
        }
    }

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

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                RingtoneListItem(
                    isRemovable = false,
                    ringtoneInfo = defInfo,
                    isPlaying = playingUri == defInfo.uri,
                    isSelected = selectedRingtone.value == defInfo.uri,
                    trailingAction = {
                        if (playingUri == defInfo.uri) {
                            player.stop()
                            playingUri = null
                        } else {
                            player.playPreview(defInfo.uri)
                            playingUri = defInfo.uri
                        }
                    }
                )
            }
            items(savedRingtones, key = { it.uri }) { ringtoneInfo ->
                RingtoneListItem(
                    ringtoneInfo = ringtoneInfo,
                    isPlaying = playingUri == ringtoneInfo.uri,
                    isSelected = selectedRingtone.value == ringtoneInfo.uri,
                    trailingAction = {
                        if (playingUri == ringtoneInfo.uri) {
                            player.stop()
                            playingUri = null
                        } else {
                            player.playPreview(ringtoneInfo.uri)
                            playingUri = ringtoneInfo.uri
                        }
                    }
                )
            }
        }

        if (savedRingtones.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Swipe to remove a ringtone",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
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

        RingtoneRandomizeTile()

    }
}

@Composable
fun RingtoneListItem(
    isRemovable: Boolean = true,
    ringtoneInfo: RingtoneInfo,
    isPlaying: Boolean,
    isSelected: Boolean,
    trailingAction: () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (!isRemovable) false
            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                AppPreferences.removeRingtone(ringtoneInfo)
                true
            }
            false
        }
    )

    SwipeToDismissBox(
        state = state,
        backgroundContent = {},
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (isSelected) 0.2f else 0.4f
                ),
            )
    ) {
        ListItem(
            modifier = Modifier.clickable{
                if (!isSelected) {
                    AppPreferences.selectedRingtone = ringtoneInfo.uri
                }
            },
            headlineContent = {
                Text(
                    text = ringtoneInfo.name,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            supportingContent = {
                if (isSelected) {
                    Text(
                        text = "Selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Tap to select",
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            trailingContent = {
                Button(onClick = trailingAction) {
                    Icon(
                        painter = painterResource(if (isPlaying) R.drawable.round_stop_24 else R.drawable.round_play_arrow_24),
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }
            },
        )
    }
}

@Composable
fun RingtoneRandomizeTile() {
    var randomizeTones by remember { mutableStateOf(AppPreferences.randomizeRingtones) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == AppPreferences.RANDOMIZE_RINGTONES_KEY) {
                randomizeTones = AppPreferences.randomizeRingtones
            }
        }
        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Randomize Ringtones",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            // Add a small spacer if needed, or rely on line height
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Ringtones are randomly selected for each alarm so you don't get bored",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Switch(
            checked = randomizeTones,
            onCheckedChange = {
                randomizeTones = it
                 AppPreferences.randomizeRingtones = it
            }
        )
    }
}

