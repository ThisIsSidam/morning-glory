package app.morning.glory.core.audio

import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri

object RingtoneHelper {

    private var currentRingtone: Ringtone? = null

    /**
     * Plays a ringtone from a given URI. If another ringtone is playing, it will be stopped first.
     */
    fun playRingtone(context: Context, uri: Uri) {
        // Stop any currently playing ringtone
        stopRingtone()

        currentRingtone = RingtoneManager.getRingtone(context, uri)
        currentRingtone?.let {
            // This is crucial to ensure the sound plays on the alarm audio stream.
            it.streamType = AudioManager.STREAM_ALARM
            it.play()
        }
    }

    /**
     * Stops the currently playing ringtone.
     */
    fun stopRingtone() {
        currentRingtone?.stop()
        currentRingtone = null
    }
}