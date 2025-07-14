package app.morning.glory.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import app.morning.glory.R

/**
 * Handles playing and controlling alarm sounds in the application.
 */
class AlarmSoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayerPlaying = false

    /**
     * Starts playing the alarm sound in a loop.
     */
    fun playAlarm() {
        if (isPlayerPlaying) {
            Log.d(TAG, "Alarm is already playing")
            return
        }

        try {
            releaseMediaPlayer()

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
                )

                // Set the data source from raw resource
                val afd = context.resources.openRawResourceFd(R.raw.alarm_sound)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()

                setOnPreparedListener {
                    isPlayerPlaying = true
                    it.isLooping = true
                    it.start()
                    Log.d(TAG, "Alarm started playing")
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Error playing alarm sound. What: $what, Extra: $extra")
                    isPlayerPlaying = false
                    false
                }

                setOnCompletionListener {
                    isPlayerPlaying = false
                    Log.d(TAG, "Alarm sound completed")
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing media player", e)
            isPlayerPlaying = false
        }
    }

    /**
     * Stops the currently playing alarm sound.
     */
    fun stopAlarm() {
        if (!isPlayerPlaying) {
            Log.d(TAG, "No alarm is currently playing")
            return
        }

        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                isPlayerPlaying = false
                Log.d(TAG, "Alarm stopped")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping alarm sound", e)
        } finally {
            releaseMediaPlayer()
        }
    }

    /**
     * Releases resources associated with the media player.
     */
    fun release() {
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            try {
                it.reset()
                it.release()
            } catch (e: Exception) {
                Log.e(TAG, "Error releasing media player", e)
            }
            mediaPlayer = null
        }
        isPlayerPlaying = false
    }

    companion object {
        private const val TAG = "AlarmSoundPlayer"
    }
}
