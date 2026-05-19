package app.morning.glory.ui.home.components.sheets

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import app.morning.glory.core.audio.AppSoundPlayer
import app.morning.glory.core.audio.RingtoneInfo
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.shared.components.SwipableItem

@Composable
fun RingtoneManagerSheetBody() {
    val context = LocalContext.current
    val player = AppSoundPlayer(context)
    val defInfo = RingtoneInfo(
        name = "Acoustic Guitar (Default)",
        uri = AppSoundPlayer.getDefaultRingtoneUri(context)
    )
    val savedRingtones = remember {
        mutableStateListOf<RingtoneInfo>(
            *AppPreferences.getRingtoneList().toTypedArray()
        )
    }
    val selectedRingtone =
        remember { mutableStateOf<Uri>(AppPreferences.selectedRingtone ?: defInfo.uri) }
    var playingUri by remember { mutableStateOf<Uri?>(null) }
    var randomizeOn by remember { mutableStateOf(AppPreferences.randomizeRingtones) }

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
            } else if (key == AppPreferences.RANDOMIZE_RINGTONES_KEY) {
                randomizeOn = AppPreferences.randomizeRingtones
            }
        }

        AppPreferences.registerListener(listener)
        onDispose {
            player.release()
            AppPreferences.unregisterListener(listener)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth(),
    ) {
        Text(
            text = "Ringtones",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                RingtoneListItem(
                    ringtoneInfo = defInfo,
                    isPlaying = playingUri == defInfo.uri,
                    isSelected = selectedRingtone.value == defInfo.uri,
                    isRandomizeOn = randomizeOn,
                    trailingAction = {
                        if (playingUri == defInfo.uri) {
                            player.stop()
                            playingUri = null
                        } else {
                            player.playPreview(defInfo.uri)
                            playingUri = defInfo.uri
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            items(savedRingtones, key = { it.uri }) { ringtoneInfo ->
                SwipableItem(
                    onDismiss = { AppPreferences.removeRingtone(ringtoneInfo) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    RingtoneListItem(
                        ringtoneInfo = ringtoneInfo,
                        isPlaying = playingUri == ringtoneInfo.uri,
                        isSelected = selectedRingtone.value == ringtoneInfo.uri,
                        isRandomizeOn = randomizeOn,
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

        OptionsTile(
            title = "Randomize Ringtones",
            description = "Play a random sound from the list above for each alarm",
            icon = Icons.Default.Shuffle,
            onClick = { AppPreferences.randomizeRingtones = !randomizeOn },
            trailingContent = {
                Switch(
                    checked = randomizeOn,
                    onCheckedChange = { AppPreferences.randomizeRingtones = it }
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (savedRingtones.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(
                text = "Swipe to remove a ringtone",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 16.dp))
    }
}
