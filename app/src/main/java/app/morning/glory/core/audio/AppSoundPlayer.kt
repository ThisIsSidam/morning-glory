package app.morning.glory.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import app.morning.glory.R
import app.morning.glory.core.utils.AppPreferences

/**
 * A unified class for handling audio playback for both alarm sounds and UI previews.
 * This class manages a single MediaPlayer instance to prevent resource conflicts.
 */
class AppSoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var isSoundPlaying = false

    /**
     * An enum to distinguish between the two playback contexts.
     */
    enum class PlaybackMode {
        ALARM, // For the actual alarm: looping.
        PREVIEW // For the ringtone picker: non-looping.
    }

    /**
     * Plays a sound from the given URI for the alarm.
     * The sound will loop until stop() is called.
     */
   fun playAlarm() {
       var ringtoneUri = AppPreferences.selectedRingtone ?: getDefaultRingtoneUri(context)
       startPlayback(ringtoneUri, PlaybackMode.ALARM)
   }

    /**
     * Plays a sound from the given URI as a preview.
     * If another sound is already playing, it will be stopped first.
     * The sound will not loop.
     */
    fun playPreview(uri: Uri) {
        // The core logic from RingtoneHelper: stop previous sound before playing the new one.
        stop()
        startPlayback(uri, PlaybackMode.PREVIEW)
    }

    private fun startPlayback(uri: Uri, mode: PlaybackMode) {
        if (isSoundPlaying) {
            Log.d(TAG, "Player is already active. Ignoring new request for now.")
            // Or call stop() here if you always want to interrupt.
            // For this design, playPreview calls stop() explicitly.
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )

                setDataSource(context, uri)

                setOnPreparedListener { player ->
                    isSoundPlaying = true
                    // Configure looping based on the mode
                    player.isLooping = (mode == PlaybackMode.ALARM)
                    player.start()
                    Log.d(TAG, "Playback started in $mode mode.")
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error. What: $what, Extra: $extra")
                    resetPlayerState()
                    true // Error was handled
                }

                setOnCompletionListener {
                    // Only relevant for non-looping sounds (PREVIEW)
                    Log.d(TAG, "Playback completed.")
                    resetPlayerState()
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MediaPlayer", e)
            resetPlayerState()
        }
    }

    /**
     * Stops the currently playing sound, regardless of mode.
     */
    fun stop() {
        if (!isSoundPlaying && mediaPlayer == null) {
            Log.d(TAG, "Player is not active. Nothing to stop.")
            return
        }

        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error stopping MediaPlayer (likely already stopped or released)", e)
        } finally {
            releaseMediaPlayer() // Clean up resources after stopping
        }
    }

    /**
     * Stops the player and Releases all resources associated with the MediaPlayer.
     * Should be called when the player is no longer needed (e.g., in onDestroy of a Service/Activity).
     */
    fun release() {
        stop()
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        resetPlayerState()
        Log.d(TAG, "MediaPlayer released.")
    }


    private fun resetPlayerState() {
        isSoundPlaying = false
    }

    companion object {
        private const val TAG = "SoundPlayer"

        fun getDefaultRingtoneUri(context: Context): Uri {
            // Return the default alarm sound URI
            return "android.resource://${context.packageName}/${R.raw.alarm_sound}".toUri()
        }
    }
}