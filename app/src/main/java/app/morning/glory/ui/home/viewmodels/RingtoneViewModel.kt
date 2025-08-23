package app.morning.glory.ui.home.viewmodels

import android.app.Application
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import app.morning.glory.core.audio.AppSoundPlayer
import app.morning.glory.core.audio.RingtoneInfo
import app.morning.glory.core.utils.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RingtoneUiState(
    val savedRingtones: List<RingtoneInfo> = emptyList(),
    val selectedRingtoneUri: Uri? = null,
    val randomizeTones: Boolean = false,
    val playingUri: Uri? = null,
    val defaultRingtone: RingtoneInfo
)

class RingtoneViewModel(application: Application) : AndroidViewModel(application) {

    private val player = AppSoundPlayer(application)
    private val defaultRingtoneInfo = RingtoneInfo(
        name = "Acoustic Guitar (Default)",
        uri = AppSoundPlayer.getDefaultRingtoneUri(application)
    )

    // Private mutable state flow, only the ViewModel can modify it.
    private val _uiState = MutableStateFlow(
        RingtoneUiState(defaultRingtone = defaultRingtoneInfo)
    )

    // Public, read-only state flow for the UI to observe.
    val uiState = _uiState.asStateFlow()

    // The SharedPreferences listener
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            AppPreferences.RINGTONE_LIST_KEY -> {
                _uiState.update { it.copy(savedRingtones = AppPreferences.getRingtoneList()) }
            }
            AppPreferences.SELECTED_RINGTONE_KEY -> {
                _uiState.update { it.copy(selectedRingtoneUri = AppPreferences.selectedRingtone) }
            }
            AppPreferences.RANDOMIZE_RINGTONES_KEY -> {
                _uiState.update { it.copy(randomizeTones = AppPreferences.randomizeRingtones) }
            }
        }
    }

    init {
        // Load initial state and register the listener when the ViewModel is created.
        loadInitialState()
        AppPreferences.registerListener(preferenceListener)
    }

    // This is called automatically when the ViewModel is destroyed.
    override fun onCleared() {
        super.onCleared()
        player.release() // Clean up resources
        AppPreferences.unregisterListener(preferenceListener)
    }

    private fun loadInitialState() {
        _uiState.value = RingtoneUiState(
            savedRingtones = AppPreferences.getRingtoneList(),
            selectedRingtoneUri = AppPreferences.selectedRingtone ?: defaultRingtoneInfo.uri,
            randomizeTones = AppPreferences.randomizeRingtones,
            defaultRingtone = defaultRingtoneInfo
        )
    }

    // --- Public functions for UI events ---

    fun addRingtone(uri: Uri) {
        val context = getApplication<Application>()
        val ringtone = RingtoneManager.getRingtone(context, uri)
        val name = ringtone.getTitle(context)
        AppPreferences.addRingtone(RingtoneInfo(name, uri))
    }

    fun removeRingtone(ringtoneInfo: RingtoneInfo) {
        AppPreferences.removeRingtone(ringtoneInfo)
    }

    fun selectRingtone(uri: Uri) {
        AppPreferences.selectedRingtone = uri
    }

    fun setRandomizeTones(isEnabled: Boolean) {
        AppPreferences.randomizeRingtones = isEnabled
    }

    fun togglePlayPause(uri: Uri) {
        val currentlyPlaying = _uiState.value.playingUri
        if (currentlyPlaying == uri) {
            player.stop()
            _uiState.update { it.copy(playingUri = null) }
        } else {
            player.playPreview(uri) {
                // When playback completes, reset the playing URI
                _uiState.update { state ->
                    if (state.playingUri == uri) state.copy(playingUri = null) else state
                }
            }
            _uiState.update { it.copy(playingUri = uri) }
        }
    }
}